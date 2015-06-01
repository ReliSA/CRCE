package cz.zcu.kiv.crce.vo.service;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.vo.model.metadata.BasicResourceVO;

/**
 * Date: 5.5.15
 *
 * @author Jakub Danek
 */
@ParametersAreNonnullByDefault
public interface MappingService {

    @Nullable
    BasicResourceVO mapBasic(Resource resource);

    @Nonnull
    List<BasicResourceVO> mapBasic(List<Resource> resources);
}
