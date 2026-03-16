package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.admin.mapper.*;
import com.atguigu.lease.web.admin.service.*;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentDetailVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentItemVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentQueryVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentSubmitVo;
import com.atguigu.lease.web.admin.vo.fee.FeeValueVo;
import com.atguigu.lease.web.admin.vo.graph.GraphVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liubo
 * @description 针对表【apartment_info(公寓信息表)】的数据库操作Service实现
 * @createDate 2023-07-24 15:48:00
 */
@Service
public class ApartmentInfoServiceImpl extends ServiceImpl<ApartmentInfoMapper, ApartmentInfo>
        implements ApartmentInfoService {

    @Autowired
    private GraphInfoService graphInfoService; // 注入图片信息服务，用于管理公寓图片

    @Autowired
    private ApartmentFacilityService apartmentFacilityService; // 注入公寓设施服务，用于管理公寓配套设施

    @Autowired
    private ApartmentLabelService apartmentLabelService; // 注入公寓标签服务，用于管理公寓标签

    @Autowired
    private ApartmentFeeValueService apartmentFeeValueService; // 注入公寓杂费服务，用于管理公寓杂费

    @Autowired
    private RoomInfoMapper roomInfoMapper;

    @Autowired
    private ApartmentInfoMapper apartmentInfoMapper;

    @Autowired
    private GraphInfoMapper graphInfoMapper;

    @Autowired
    private LabelInfoMapper labelInfoMapper;

    @Autowired
    private FacilityInfoMapper facilityInfoMapper;

    @Autowired
    private FeeValueMapper feeValueMapper;



    // 保存或更新公寓信息
    @Override
    public void saveOrUpdateApartment(ApartmentSubmitVo apartmentSubmitVo) {
        boolean isUpdate = apartmentSubmitVo.getId() != null; // 判断是否为更新操作：如果ID不为空，则为更新
        super.saveOrUpdate(apartmentSubmitVo); // 调用父类方法保存或更新公寓基本信息

        if (isUpdate) { // 如果是更新操作，需要先删除原有关联数据
            // 1. 删除公寓相关的图片列表
            LambdaQueryWrapper<GraphInfo> graphQueryWrapper = new LambdaQueryWrapper<>();
            graphQueryWrapper.eq(GraphInfo::getItemType, ItemType.APARTMENT); // 条件：图片类型为公寓
            graphQueryWrapper.eq(GraphInfo::getItemId, apartmentSubmitVo.getId()); // 条件：图片关联的公寓ID
            graphInfoService.remove(graphQueryWrapper); // 执行删除操作

            // 2. 删除公寓相关的配套设施列表
            LambdaQueryWrapper<ApartmentFacility> facilityQueryWrapper = new LambdaQueryWrapper<>();
            facilityQueryWrapper.eq(ApartmentFacility::getApartmentId, apartmentSubmitVo.getId()); // 条件：设施关联的公寓ID
            apartmentFacilityService.remove(facilityQueryWrapper); // 执行删除操作

            // 3. 删除公寓相关的标签列表
            LambdaQueryWrapper<ApartmentLabel> labelQueryWrapper = new LambdaQueryWrapper<>();
            labelQueryWrapper.eq(ApartmentLabel::getApartmentId, apartmentSubmitVo.getId()); // 条件：标签关联的公寓ID
            apartmentLabelService.remove(labelQueryWrapper); // 执行删除操作

            // 4. 删除公寓相关的杂费列表
            LambdaQueryWrapper<ApartmentFeeValue> feeQueryWrapper = new LambdaQueryWrapper<>();
            feeQueryWrapper.eq(ApartmentFeeValue::getApartmentId, apartmentSubmitVo.getId()); // 条件：杂费关联的公寓ID
            apartmentFeeValueService.remove(feeQueryWrapper); // 执行删除操作
        }

        // 插入新的关联数据
        // 1. 插入公寓图片列表
        List<GraphVo> graphVoList = apartmentSubmitVo.getGraphVoList(); // 获取提交的图片列表
        if (!CollectionUtils.isEmpty(graphVoList)) { // 如果图片列表不为空
            ArrayList<GraphInfo> graphInfoList = new ArrayList<>(); // 创建图片信息列表
            for (GraphVo graphVo : graphVoList) { // 遍历每个图片VO
                GraphInfo graphInfo = new GraphInfo(); // 创建图片信息实体
                graphInfo.setItemType(ItemType.APARTMENT); // 设置图片类型为公寓
                graphInfo.setItemId(apartmentSubmitVo.getId()); // 设置关联的公寓ID
                graphInfo.setName(graphVo.getName()); // 设置图片名称
                graphInfo.setUrl(graphVo.getUrl()); // 设置图片URL
                graphInfoList.add(graphInfo); // 添加到列表
            }
            graphInfoService.saveBatch(graphInfoList); // 批量保存图片信息
        }

        // 2. 插入公寓配套设施列表
        List<Long> facilityInfoIdList = apartmentSubmitVo.getFacilityInfoIds(); // 获取设施ID列表
        if (!CollectionUtils.isEmpty(facilityInfoIdList)) { // 如果设施ID列表不为空
            ArrayList<ApartmentFacility> facilityList = new ArrayList<>(); // 创建设施列表
            for (Long facilityId : facilityInfoIdList) { // 遍历每个设施ID
                ApartmentFacility apartmentFacility = ApartmentFacility.builder() // 使用建造者模式创建设施实体
                        .apartmentId(apartmentSubmitVo.getId()) // 设置公寓ID
                        .facilityId(facilityId) // 设置设施ID
                        .build(); // 构建对象
                facilityList.add(apartmentFacility); // 添加到列表
            }
            apartmentFacilityService.saveBatch(facilityList); // 批量保存设施信息
        }

        // 3. 插入公寓标签列表
        List<Long> labelIds = apartmentSubmitVo.getLabelIds(); // 获取标签ID列表
        if (!CollectionUtils.isEmpty(labelIds)) { // 如果标签ID列表不为空
            List<ApartmentLabel> apartmentLabelList = new ArrayList<>(); // 创建标签列表
            for (Long labelId : labelIds) { // 遍历每个标签ID
                ApartmentLabel apartmentLabel = ApartmentLabel.builder() // 使用建造者模式创建标签实体
                        .apartmentId(apartmentSubmitVo.getId()) // 设置公寓ID
                        .labelId(labelId) // 设置标签ID
                        .build(); // 构建对象
                apartmentLabelList.add(apartmentLabel); // 添加到列表
            }
            apartmentLabelService.saveBatch(apartmentLabelList); // 批量保存标签信息
        }

        // 4. 插入公寓杂费列表
        List<Long> feeValueIds = apartmentSubmitVo.getFeeValueIds(); // 获取杂费ID列表
        if (!CollectionUtils.isEmpty(feeValueIds)) { // 如果杂费ID列表不为空
            ArrayList<ApartmentFeeValue> apartmentFeeValueList = new ArrayList<>(); // 创建杂费列表
            for (Long feeValueId : feeValueIds) { // 遍历每个杂费ID
                ApartmentFeeValue apartmentFeeValue = ApartmentFeeValue.builder() // 使用建造者模式创建杂费实体
                        .apartmentId(apartmentSubmitVo.getId()) // 设置公寓ID
                        .feeValueId(feeValueId) // 设置杂费ID
                        .build(); // 构建对象
                apartmentFeeValueList.add(apartmentFeeValue); // 添加到列表
            }
            apartmentFeeValueService.saveBatch(apartmentFeeValueList); // 批量保存杂费信息
        }
    }

    @Override
    public IPage<ApartmentItemVo> pageApartmentItemByQuery(IPage<ApartmentItemVo> page, ApartmentQueryVo queryVo) {
        return apartmentInfoMapper.pageApartmentItemByQuery(page, queryVo);
    }

    @Override
    public ApartmentDetailVo getApartmentDetailById(Long id) {
        //1.查询ApartmentInfo
        ApartmentInfo apartmentInfo = this.getById(id);
        if (apartmentInfo == null) {
            return null;
        }

        //2.查询GraphInfo
        List<GraphVo> graphVoList = graphInfoMapper.selectListByItemTypeAndId(ItemType.APARTMENT, id);

        //3.查询LabelInfo
        List<LabelInfo> labelInfoList = labelInfoMapper.selectListByApartmentId(id);

        //4.查询FacilityInfo
        List<FacilityInfo> facilityInfoList = facilityInfoMapper.selectListByApartmentId(id);

        //5.查询FeeValue
        List<FeeValueVo> feeValueVoList = feeValueMapper.selectListByApartmentId(id);

        ApartmentDetailVo adminApartmentDetailVo = new ApartmentDetailVo();

        BeanUtils.copyProperties(apartmentInfo, adminApartmentDetailVo);
        adminApartmentDetailVo.setGraphVoList(graphVoList);
        adminApartmentDetailVo.setLabelInfoList(labelInfoList);
        adminApartmentDetailVo.setFacilityInfoList(facilityInfoList);
        adminApartmentDetailVo.setFeeValueVoList(feeValueVoList);

        return adminApartmentDetailVo;

    }

    @Override
    public void removeApartmentById(Long id) {

        LambdaQueryWrapper<RoomInfo> roomQueryWrapper = new LambdaQueryWrapper<>();
        roomQueryWrapper.eq(RoomInfo::getApartmentId, id);
        Long count = roomInfoMapper.selectCount(roomQueryWrapper);
        if (count > 0) {
            throw new LeaseException(ResultCodeEnum.ADMIN_APARTMENT_DELETE_ERROR);
        }

        //1.删除GraphInfo
        LambdaQueryWrapper<GraphInfo> graphQueryWrapper = new LambdaQueryWrapper<>();
        graphQueryWrapper.eq(GraphInfo::getItemType, ItemType.APARTMENT);
        graphQueryWrapper.eq(GraphInfo::getItemId, id);
        graphInfoService.remove(graphQueryWrapper);

        //2.删除ApartmentLabel
        LambdaQueryWrapper<ApartmentLabel> labelQueryWrapper = new LambdaQueryWrapper<>();
        labelQueryWrapper.eq(ApartmentLabel::getApartmentId, id);
        apartmentLabelService.remove(labelQueryWrapper);

        //3.删除ApartmentFacility
        LambdaQueryWrapper<ApartmentFacility> facilityQueryWrapper = new LambdaQueryWrapper<>();
        facilityQueryWrapper.eq(ApartmentFacility::getApartmentId, id);
        apartmentFacilityService.remove(facilityQueryWrapper);

        //4.删除ApartmentFeeValue
        LambdaQueryWrapper<ApartmentFeeValue> feeQueryWrapper = new LambdaQueryWrapper<>();
        feeQueryWrapper.eq(ApartmentFeeValue::getApartmentId, id);
        apartmentFeeValueService.remove(feeQueryWrapper);

        //5.删除ApartmentInfo
        super.removeById(id);
    }
}