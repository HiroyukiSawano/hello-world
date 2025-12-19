package org.example.myjweb.service;

import java.util.Optional;

import org.example.myjweb.entity.ApiRequest;
import org.example.myjweb.entity.RequestVersion;
import org.example.myjweb.repository.ApiRequestMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApiRequestService {

    private final ApiRequestMapper apiRequestMapper;
    private final RequestVersionService requestVersionService;

    public ApiRequestService(ApiRequestMapper apiRequestMapper, RequestVersionService requestVersionService) {
        this.apiRequestMapper = apiRequestMapper;
        this.requestVersionService = requestVersionService;
    }

    @Transactional
    public RequestVersion createRequestWithVersion(RequestVersion draft) {
        ServiceValidator.requireObject(draft, "请求版本不能为空");
        ApiRequest request = new ApiRequest();
        apiRequestMapper.insert(request);
        return requestVersionService.createInitialVersion(request.getId(), draft);
    }

    public Optional<ApiRequest> findById(Long id) {
        ServiceValidator.requireId(id, "请求ID不能为空");
        return Optional.ofNullable(apiRequestMapper.selectById(id));
    }

    public boolean delete(Long id) {
        ServiceValidator.requireId(id, "请求ID不能为空");
        return apiRequestMapper.deleteById(id) > 0;
    }
}
