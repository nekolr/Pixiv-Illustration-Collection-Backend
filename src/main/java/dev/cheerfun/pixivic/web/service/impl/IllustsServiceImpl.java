package dev.cheerfun.pixivic.web.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.cheerfun.pixivic.web.mapper.IllustsMapper;
import dev.cheerfun.pixivic.web.service.IIllustsService;
import dev.cheerfun.pixivic.web.model.Illusts;
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
