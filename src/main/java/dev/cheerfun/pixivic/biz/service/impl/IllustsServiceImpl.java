package dev.cheerfun.pixivic.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.cheerfun.pixivic.biz.mapper.IllustsMapper;
import dev.cheerfun.pixivic.biz.service.IIllustsService;
import dev.cheerfun.pixivic.model.entity.Illusts;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 插画表 服务实现类
 * </p>
 *
 * @author huangsm
 * @since 2019-06-28
 */
@Service
public class IllustsServiceImpl extends ServiceImpl<IllustsMapper, Illusts> implements IIllustsService {

}
