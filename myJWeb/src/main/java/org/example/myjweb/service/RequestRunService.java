package org.example.myjweb.service;

import java.util.List;
import java.util.Optional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.myjweb.entity.RequestRun;
import org.example.myjweb.repository.RequestRunMapper;
import org.springframework.stereotype.Service;

@Service
public class RequestRunService {

    private final RequestRunMapper requestRunMapper;

    public RequestRunService(RequestRunMapper requestRunMapper) {
        this.requestRunMapper = requestRunMapper;
    }

    public RequestRun record(RequestRun run) {
        ServiceValidator.requireObject(run, "请求执行记录不能为空");
        ServiceValidator.requireId(run.getRequestVersionId(), "请求版本ID不能为空");
        run.setId(null);
        if (run.getResponseBody() != null && run.getResponseSize() == null) {
            // 根据响应体大小补齐响应长度
            run.setResponseSize((long) run.getResponseBody().length);
        }
        requestRunMapper.insert(run);
        return run;
    }

    public Optional<RequestRun> findById(Long id) {
        ServiceValidator.requireId(id, "请求执行ID不能为空");
        return Optional.ofNullable(requestRunMapper.selectById(id));
    }

    public List<RequestRun> listByRequestVersionId(Long requestVersionId) {
        ServiceValidator.requireId(requestVersionId, "请求版本ID不能为空");
        return requestRunMapper.selectList(new LambdaQueryWrapper<RequestRun>()
                .eq(RequestRun::getRequestVersionId, requestVersionId)
                .orderByDesc(RequestRun::getCreatedAt));
    }

    public List<RequestRun> listByEnvironmentId(Long environmentId) {
        ServiceValidator.requireId(environmentId, "环境ID不能为空");
        return requestRunMapper.selectList(new LambdaQueryWrapper<RequestRun>()
                .eq(RequestRun::getEnvironmentId, environmentId)
                .orderByDesc(RequestRun::getCreatedAt));
    }

    public boolean delete(Long id) {
        ServiceValidator.requireId(id, "请求执行ID不能为空");
        return requestRunMapper.deleteById(id) > 0;
    }
}
