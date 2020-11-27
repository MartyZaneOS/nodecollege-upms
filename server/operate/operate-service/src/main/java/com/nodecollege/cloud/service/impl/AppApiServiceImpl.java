package com.nodecollege.cloud.service.impl;

import com.nodecollege.cloud.common.annotation.ApiAnnotation;
import com.nodecollege.cloud.common.aop.NCAop;
import com.nodecollege.cloud.common.constants.RedisConstants;
import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.MenuVO;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.OperateAppApi;
import com.nodecollege.cloud.common.model.po.OperateProduct;
import com.nodecollege.cloud.common.model.po.OperateProductMenu;
import com.nodecollege.cloud.common.model.vo.ControllerVO;
import com.nodecollege.cloud.common.model.vo.InterfaceTreeVO;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.common.utils.RedisUtils;
import com.nodecollege.cloud.dao.mapper.OperateAppApiMapper;
import com.nodecollege.cloud.service.AppApiService;
import com.nodecollege.cloud.service.ProductMenuService;
import com.nodecollege.cloud.service.ProductService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * 接口 service
 *
 * @author LC
 * @date 2019/12/20 18:41
 */
@Service
public class AppApiServiceImpl implements AppApiService {

    private static final Logger logger = LoggerFactory.getLogger(AppApiServiceImpl.class);

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Autowired
    private OperateAppApiMapper operateAppApiMapper;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductMenuService productMenuService;

    @Value("${spring.application.name}")
    private String applicationName;

    /**
     * 项目启动时，自动查询用注解标记过的接口数据，并和数据库进行比对，有则更新，无则插入
     */
    @PostConstruct
    private void initApiList() {
        logger.info("初始化查询api信息！");
        List<OperateAppApi> annList = new ArrayList<>();
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();

        for (Map.Entry<RequestMappingInfo, HandlerMethod> m : handlerMethods.entrySet()) {
            RequestMappingInfo info = m.getKey();
            HandlerMethod method = m.getValue();
            ApiAnnotation apiAnnotation = method.getMethodAnnotation(ApiAnnotation.class);
            OperateAppApi api = new OperateAppApi();
            api.setApplicationName(applicationName);
            api.setControllerName(NCAop.getConciseMethodName(method.getMethod().getDeclaringClass().getName()));
            Set<RequestMethod> temp = info.getMethodsCondition().getMethods();
            api.setMethod(temp.isEmpty() ? "" : temp.iterator().next().name());
            api.setApiUrl(info.getPatternsCondition().getPatterns().iterator().next());
            if (apiAnnotation != null) {
                api.setModularName(apiAnnotation.modularName());
                api.setDescription(apiAnnotation.description());
                api.setState(apiAnnotation.state());
            } else {
                api.setModularName(api.getControllerName());
                api.setDescription(api.getApiUrl());
                api.setState(1);
            }
            api.setAccessAuth("none");
            logger.info("api信息 {}", api.toString());
            annList.add(api);
        }
        initApiList(annList);
    }

    @Override
    public List<OperateAppApi> getAppApiList(QueryVO<OperateAppApi> queryVO) {
        return operateAppApiMapper.selectListByMap(queryVO.toMap());
    }

    /**
     * 获取接口树
     *
     * @return
     */
    @Override
    public List<InterfaceTreeVO> getApiTree() {
        // 原始接口列表
        List<OperateAppApi> list = getApiListCache();
        // 树列表
        List<InterfaceTreeVO> treeVOList = new ArrayList<>();

        Iterator<OperateAppApi> iterator = list.iterator();
        c:
        while (iterator.hasNext()) {
            OperateAppApi api = iterator.next();
            for (int i = 0; i < treeVOList.size(); i++) {
                InterfaceTreeVO treeVO = treeVOList.get(i);
                if (treeVO.getApplicationName().equals(api.getApplicationName())) {
                    for (int j = 0; j < treeVO.getChildren().size(); j++) {
                        ControllerVO cVo = treeVO.getChildren().get(j);
                        if (cVo.getControllerName().equals(api.getControllerName())) {
                            cVo.getChildren().add(api);
                            iterator.remove();
                            continue c;
                        }
                    }
                    List<OperateAppApi> interfaceList = new ArrayList<>();
                    interfaceList.add(api);

                    ControllerVO cVo = new ControllerVO();
                    cVo.setApplicationName(api.getApplicationName());
                    cVo.setControllerName(api.getControllerName());
                    cVo.setChildren(interfaceList);

                    treeVO.getChildren().add(cVo);
                    iterator.remove();
                    continue c;
                }
            }

            List<OperateAppApi> interfaceList = new ArrayList<>();
            interfaceList.add(api);

            ControllerVO cVo = new ControllerVO();
            cVo.setApplicationName(api.getApplicationName());
            cVo.setControllerName(api.getControllerName());
            cVo.setChildren(interfaceList);

            List<ControllerVO> cVoList = new ArrayList<>();
            cVoList.add(cVo);

            InterfaceTreeVO treeVO = new InterfaceTreeVO();
            treeVO.setApplicationName(api.getApplicationName());
            treeVO.setChildren(cVoList);
            treeVOList.add(treeVO);

            iterator.remove();
        }
        return treeVOList;
    }

    /**
     * 查询接口列表缓存
     */
    @Override
    public List<OperateAppApi> getApiListCache() {
        List<OperateAppApi> list = redisUtils.getList(RedisConstants.APP_API_LIST, OperateAppApi.class);
        if (list != null) {
            return list;
        }
        list = operateAppApiMapper.selectListByMap(null);
        redisUtils.set(RedisConstants.APP_API_LIST, list, -1L);
        return list;
    }

    /**
     * 清除接口列表缓存
     */
    @Override
    public void clearApiListCache() {
        redisUtils.delete(RedisConstants.APP_API_LIST);
        getApiListCache();
        redisUtils.increment(RedisConstants.APP_API_VERSION, 1);
    }

    /**
     * 根据传入的接口列表，查询数据库，在数据库中不存在，则将记录插入数据库，存在跳过
     */
    @Override
    public List<OperateAppApi> initApiList(List<OperateAppApi> annList) {
        if (NCUtils.isNullOrEmpty(annList)) {
            return new ArrayList<>();
        }
        QueryVO<OperateAppApi> queryVO = new QueryVO<>(new OperateAppApi());
        queryVO.getData().setApplicationName(annList.get(0).getApplicationName());
        List<OperateAppApi> sqlList = operateAppApiMapper.selectListByMap(queryVO.toMap());
        List<OperateAppApi> returnList = new ArrayList<>(annList.size());
        for (int i = 0; i < annList.size(); i++) {
            Iterator<OperateAppApi> iterator = sqlList.iterator();
            boolean ex = false;
            while (iterator.hasNext()) {
                OperateAppApi sqlItem = iterator.next();
                if (annList.get(i).getApplicationName().equals(sqlItem.getApplicationName())
                        && annList.get(i).getControllerName().equals(sqlItem.getControllerName())
                        && annList.get(i).getApiUrl().equals(sqlItem.getApiUrl())) {
                    // 数据库中已存在
                    ex = true;
                    if (!annList.get(i).getModularName().equals(sqlItem.getModularName())
                            || !annList.get(i).getDescription().equals(sqlItem.getDescription())
                            || !annList.get(i).getState().equals(sqlItem.getState())) {
                        // 模块名称或者 描述不同 更新数据
                        annList.get(i).setApiId(sqlItem.getApiId());
                        operateAppApiMapper.updateByPrimaryKey(annList.get(i));
                    }
                    returnList.add(sqlItem);
                    iterator.remove();
                    break;
                }
            }
            // 不存在 添加
            if (!ex) {
                operateAppApiMapper.insert(annList.get(i));
                returnList.add(annList.get(i));
            }
        }
        // 将删除的api状态置为已删除
        for (int i = 0; i < sqlList.size(); i++) {
            if (-1 != sqlList.get(i).getState()) {
                sqlList.get(i).setState(-1);
                operateAppApiMapper.updateByPrimaryKey(sqlList.get(i));
            }
        }
        clearApiListCache();
        return returnList;
    }

    /**
     * 删除接口
     */
    @Override
    public void delApi(OperateAppApi api) {
        NCUtils.nullOrEmptyThrow(api.getApiId());
        OperateAppApi ex = operateAppApiMapper.selectByPrimaryKey(api.getApiId());
        NCUtils.nullOrEmptyThrow(ex, new NCException("-1", "该接口不存在！"));
        if (ex.getState() != -1) {
            throw new NCException("", "不能删除该接口！");
        }
        operateAppApiMapper.deleteByPrimaryKey(api.getApiId());
        clearApiListCache();
    }

    /**
     * 更新接口授权信息
     */
    @Override
    public void syncAllAppApiAccessAuth() {
        // 查询所有的绑定产品的菜单信息
        OperateProduct queryProduct = new OperateProduct();
        queryProduct.setProductType(1);
        List<OperateProduct> productList = productService.getProductList(queryProduct);
        Map<String, Integer> adminUrlMap = new HashMap<>();
        Map<String, Integer> userUrlMap = new HashMap<>();
        Map<String, Integer> memberUrlMap = new HashMap<>();

        for (int i = 0; i < productList.size(); i++) {
            OperateProductMenu queryMenu = new OperateProductMenu();
            queryMenu.setProductCode(productList.get(i).getProductCode());
            List<MenuVO> menuVOList = productMenuService.getProductMenuList(queryMenu);
            for (int j = 0; j < menuVOList.size(); j++) {
                if (menuVOList.get(j).getMenuType() > 1) {
                    String path = menuVOList.get(j).getAppName() + "/" + menuVOList.get(j).getApiUrl();
                    if (productList.get(i).getProductUsage() == 0) {
                        adminUrlMap.put(path, 1);
                    }
                    if (productList.get(i).getProductUsage() == 1) {
                        userUrlMap.put(path, 1);
                    }
                    if (productList.get(i).getProductUsage() == 2) {
                        memberUrlMap.put(path, 1);
                    }
                }
            }
        }

        List<OperateAppApi> apiList = getApiListCache();
        for (int i = 0; i < apiList.size(); i++) {
            OperateAppApi temp = apiList.get(i);
            String key = temp.getApplicationName() + "/" + temp.getApiUrl();
            if (StringUtils.isBlank(temp.getAccessAuth())) {
                temp.setAccessAuth("none");
            }
            Set<String> accessAuth = new HashSet<>(Arrays.asList(temp.getAccessAuth().split(",")));
            if (adminUrlMap.containsKey(key)) {
                accessAuth.add("adminAuth");
            } else {
                accessAuth.remove("adminAuth");
            }
            if (userUrlMap.containsKey(key)) {
                accessAuth.add("userAuth");
            } else {
                accessAuth.remove("userAuth");
            }
            if (memberUrlMap.containsKey(key)) {
                accessAuth.add("memberAuth");
            } else {
                accessAuth.remove("memberAuth");
            }
            if (accessAuth.contains("adminAuth") || accessAuth.contains("adminLogin")
                    || accessAuth.contains("userAuth") || accessAuth.contains("userLogin")
                    || accessAuth.contains("memberAuth") || accessAuth.contains("memberLogin")) {
                accessAuth.remove("inner");
                accessAuth.remove("none");
            }
            if (accessAuth.contains("inner")) {
                accessAuth.remove("none");
            }
            if (accessAuth.contains("none")) {
                accessAuth.remove("inner");
            }
            temp.setAccessAuth(String.join(",", accessAuth));
            operateAppApiMapper.updateByPrimaryKeySelective(temp);
        }
        clearApiListCache();
    }

    @Override
    public void updateAppApiAccessAuth(OperateAppApi appApi) {
        NCUtils.nullOrEmptyThrow(appApi.getApiId());
        NCUtils.nullOrEmptyThrow(appApi.getAccessAuth());
        OperateAppApi ex = operateAppApiMapper.selectByPrimaryKey(appApi.getApiId());
        NCUtils.nullOrEmptyThrow(ex, "", "接口不存在！");

        // 授权控制只能通过 syncAllAppApiAccessAuth 接口进行更新
        Set<String> updateAuth = new HashSet<>(Arrays.asList(appApi.getAccessAuth().split(",")));
        Set<String> exAuth = new HashSet<>(Arrays.asList(ex.getAccessAuth().split(",")));
        if (exAuth.contains("adminAuth")) {
            updateAuth.add("adminAuth");
        } else {
            updateAuth.remove("adminAuth");
        }
        if (exAuth.contains("userAuth")) {
            updateAuth.add("userAuth");
        } else {
            updateAuth.remove("userAuth");
        }
        if (exAuth.contains("memberAuth")) {
            updateAuth.add("memberAuth");
        } else {
            updateAuth.remove("memberAuth");
        }
        if (updateAuth.contains("adminAuth") || updateAuth.contains("adminLogin")
                || updateAuth.contains("userAuth") || updateAuth.contains("userLogin")
                || updateAuth.contains("memberAuth") || updateAuth.contains("memberLogin")) {
            updateAuth.remove("inner");
            updateAuth.remove("none");
        }
        if (updateAuth.contains("inner")) {
            updateAuth.remove("none");
        }
        if (updateAuth.contains("none")) {
            updateAuth.remove("inner");
        }
        OperateAppApi update = new OperateAppApi();
        update.setApiId(ex.getApiId());
        update.setAccessAuth(String.join(",", updateAuth));
        operateAppApiMapper.updateByPrimaryKeySelective(update);
        clearApiListCache();
    }
}
