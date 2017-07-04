package cz.zcu.kiv.crce.vo.service;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import cz.zcu.kiv.crce.compatibility.Compatibility;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.resolver.optimizer.CostFunctionFactory;
import cz.zcu.kiv.crce.resolver.optimizer.ResultOptimizer;
import cz.zcu.kiv.crce.vo.model.compatibility.CompatibilityVO;
import cz.zcu.kiv.crce.vo.model.metadata.BasicResourceVO;
import cz.zcu.kiv.crce.vo.model.metadata.DetailedResourceVO;
import cz.zcu.kiv.crce.vo.model.metadata.GenericRequirementVO;
import cz.zcu.kiv.crce.vo.model.optimizer.CostFunctionDescriptorVO;
import cz.zcu.kiv.crce.vo.model.optimizer.ResultOptimizerVO;

/**
 *
 * Service for mapping CRCE inner representation APIs into
 * value objects.
 *
 * Date: 5.5.15
 *
 * @author Jakub Danek
 */
@ParametersAreNonnullByDefault
public interface MappingService {

    /**
     * Maps single resource into BasicResourceVO which contains
     * only identity information about the resource.
     * @param resource
     * @return
     */
    @Nullable
    BasicResourceVO mapBasic(@Nullable Resource resource);

    /**
     * Maps list of resources into BasicResourceVos which
     * contain only identity of the resources.
     * @param resources
     * @return
     */
    @Nonnull
    List<BasicResourceVO> mapBasic(List<Resource> resources);

    /**
     * Maps resource into VO including all details.
     *
     * @param resource
     * @return
     */
    @Nullable
    DetailedResourceVO mapFull(@Nullable Resource resource);

    /**
     * Maps list of resource into VOs including all details.
     *
     * @param resources
     * @return
     */
    @Nonnull
    List<DetailedResourceVO> mapFull(List<Resource> resources);

    /**
     * Maps list of compatibility metadata into VOs
     * @param diffs metadata to be mapped
     * @return
     */
    @Nonnull
    List<CompatibilityVO> mapCompatibility(List<Compatibility> diffs);

    /**
     * Maps single piece of compatibility meta-data into VO
     * @param diff piece of meta-data to be mapped
     * @return
     */
    @Nullable
    CompatibilityVO mapCompatibility(@Nullable Compatibility diff);

    /**
     * Maps list of requirement vos into list of requirement entities.
     *
     * @param requirements to be mapped, not null
     * @return
     */
    @Nonnull
    List<Requirement> map(List<GenericRequirementVO> requirements);

    @Nonnull
    List<CostFunctionDescriptorVO> mapCostFunction(List<CostFunctionFactory> descriptors);

    @Nullable
    CostFunctionDescriptorVO mapCostFunction(@Nullable CostFunctionFactory descriptor);

    @Nonnull
    List<ResultOptimizerVO> mapResultOptimizer(List<ResultOptimizer> descriptors);

    @Nullable
    ResultOptimizerVO mapResultOptimizer(@Nullable ResultOptimizer descriptor);

}
