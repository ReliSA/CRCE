package cz.zcu.kiv.crce.rest.v2.internal.ws;

import java.util.List;

import cz.zcu.kiv.crce.vo.model.optimizer.ResultOptimizerVO;

/**
 * Date: 17.6.16
 *
 * @author Jakub Danek
 */
public interface OptimizerRes {

    ResultOptimizerVO findOne(String id);

    List<ResultOptimizerVO> list();

}
