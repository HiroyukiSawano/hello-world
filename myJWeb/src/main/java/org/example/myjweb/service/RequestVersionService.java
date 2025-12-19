package org.example.myjweb.service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.myjweb.entity.RequestVersion;
import org.example.myjweb.repository.ApiRequestMapper;
import org.example.myjweb.repository.RequestVersionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class RequestVersionService {

    private final RequestVersionMapper requestVersionMapper;
    private final ApiRequestMapper apiRequestMapper;

    public RequestVersionService(RequestVersionMapper requestVersionMapper, ApiRequestMapper apiRequestMapper) {
        this.requestVersionMapper = requestVersionMapper;
        this.apiRequestMapper = apiRequestMapper;
    }

    @Transactional
    public RequestVersion createInitialVersion(Long requestId, RequestVersion draft) {
        return createVersion(requestId, draft, 1);
    }

    @Transactional
    public RequestVersion createNextVersion(Long requestId, RequestVersion draft) {
        int nextVersion = resolveNextVersion(requestId);
        return createVersion(requestId, draft, nextVersion);
    }

    public Optional<RequestVersion> findById(Long id) {
        ServiceValidator.requireId(id, "请求版本ID不能为空");
        return Optional.ofNullable(requestVersionMapper.selectById(id));
    }

    public List<RequestVersion> listByRequestId(Long requestId) {
        ServiceValidator.requireId(requestId, "请求ID不能为空");
        return requestVersionMapper.selectList(new LambdaQueryWrapper<RequestVersion>()
                .eq(RequestVersion::getRequestId, requestId)
                .orderByDesc(RequestVersion::getVersion));
    }

    public Optional<RequestVersion> findLatestByRequestId(Long requestId) {
        ServiceValidator.requireId(requestId, "请求ID不能为空");
        return Optional.ofNullable(requestVersionMapper.selectOne(new LambdaQueryWrapper<RequestVersion>()
                .eq(RequestVersion::getRequestId, requestId)
                .orderByDesc(RequestVersion::getVersion)
                .last("limit 1")));
    }

    public boolean update(RequestVersion version) {
        ServiceValidator.requireObject(version, "请求版本不能为空");
        ServiceValidator.requireId(version.getId(), "请求版本ID不能为空");
        if (version.getName() != null) {
            ServiceValidator.requireText(version.getName(), "请求名称不能为空");
            version.setName(version.getName().trim());
        }
        if (version.getMethod() != null) {
            ServiceValidator.requireText(version.getMethod(), "HTTP方法不能为空");
            version.setMethod(version.getMethod().trim().toUpperCase(Locale.ROOT));
        }
        if (version.getUrl() != null) {
            ServiceValidator.requireText(version.getUrl(), "请求地址不能为空");
            version.setUrl(version.getUrl().trim());
        }
        if (version.getDescription() != null) {
            version.setDescription(StringUtils.trimWhitespace(version.getDescription()));
        }
        if (version.getNote() != null) {
            version.setNote(StringUtils.trimWhitespace(version.getNote()));
        }
        return requestVersionMapper.updateById(version) > 0;
    }

    public boolean delete(Long id) {
        ServiceValidator.requireId(id, "请求版本ID不能为空");
        return requestVersionMapper.deleteById(id) > 0;
    }

    private RequestVersion createVersion(Long requestId, RequestVersion draft, int version) {
        ServiceValidator.requireId(requestId, "请求ID不能为空");
        ServiceValidator.requireObject(draft, "请求版本不能为空");
        requireRequestExists(requestId);
        normalizeDraft(draft);
        draft.setId(null);
        draft.setRequestId(requestId);
        draft.setVersion(version);
        requestVersionMapper.insert(draft);
        return draft;
    }

    private int resolveNextVersion(Long requestId) {
        ServiceValidator.requireId(requestId, "请求ID不能为空");
        RequestVersion latest = requestVersionMapper.selectOne(new LambdaQueryWrapper<RequestVersion>()
                .eq(RequestVersion::getRequestId, requestId)
                .orderByDesc(RequestVersion::getVersion)
                .last("limit 1"));
        // 读取当前最大版本号，生成下一个版本号
        return latest == null || latest.getVersion() == null ? 1 : latest.getVersion() + 1;
    }

    private void requireRequestExists(Long requestId) {
        if (apiRequestMapper.selectById(requestId) == null) {
            throw new IllegalArgumentException("请求不存在");
        }
    }

    private void normalizeDraft(RequestVersion draft) {
        ServiceValidator.requireText(draft.getName(), "请求名称不能为空");
        ServiceValidator.requireText(draft.getMethod(), "HTTP方法不能为空");
        ServiceValidator.requireText(draft.getUrl(), "请求地址不能为空");
        draft.setName(draft.getName().trim());
        draft.setMethod(draft.getMethod().trim().toUpperCase(Locale.ROOT));
        draft.setUrl(draft.getUrl().trim());
        if (draft.getDescription() != null) {
            draft.setDescription(StringUtils.trimWhitespace(draft.getDescription()));
        }
        if (draft.getNote() != null) {
            draft.setNote(StringUtils.trimWhitespace(draft.getNote()));
        }
    }
}
