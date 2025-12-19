-- MyJWeb 接口测试工具库表 (MySQL 8)

CREATE TABLE projects (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    name VARCHAR(128) NOT NULL COMMENT '项目名称',
    description VARCHAR(512) COMMENT '项目描述',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目';

CREATE TABLE environments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    project_id BIGINT NOT NULL COMMENT '所属项目ID',
    name VARCHAR(128) NOT NULL COMMENT '环境名称',
    is_default TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否默认环境',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT fk_environments_project
        FOREIGN KEY (project_id) REFERENCES projects(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='环境';

CREATE TABLE environment_variables (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    environment_id BIGINT NOT NULL COMMENT '所属环境ID',
    name VARCHAR(128) NOT NULL COMMENT '变量名',
    value TEXT COMMENT '变量值',
    is_secret TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否敏感',
    is_enabled TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序号',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT fk_env_vars_environment
        FOREIGN KEY (environment_id) REFERENCES environments(id)
        ON DELETE CASCADE,
    UNIQUE KEY uk_env_vars_name (environment_id, name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='环境变量';

CREATE TABLE collections (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    project_id BIGINT NOT NULL COMMENT '所属项目ID',
    name VARCHAR(128) NOT NULL COMMENT '集合名称',
    description VARCHAR(512) COMMENT '集合描述',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序号',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT fk_collections_project
        FOREIGN KEY (project_id) REFERENCES projects(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='集合';

CREATE TABLE folders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    collection_id BIGINT NOT NULL COMMENT '所属集合ID',
    parent_id BIGINT COMMENT '父目录ID',
    name VARCHAR(128) NOT NULL COMMENT '目录名称',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序号',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT fk_folders_collection
        FOREIGN KEY (collection_id) REFERENCES collections(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_folders_parent
        FOREIGN KEY (parent_id) REFERENCES folders(id)
        ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='目录';

CREATE TABLE requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='请求';

CREATE TABLE request_versions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    request_id BIGINT NOT NULL COMMENT '请求ID',
    version INT NOT NULL COMMENT '版本号',
    name VARCHAR(128) NOT NULL COMMENT '请求名称',
    folder_id BIGINT COMMENT '所属目录ID',
    method VARCHAR(16) NOT NULL COMMENT 'HTTP方法',
    url TEXT NOT NULL COMMENT '请求地址',
    description TEXT COMMENT '请求描述',
    timeout_ms INT NOT NULL DEFAULT 0 COMMENT '超时毫秒',
    follow_redirects TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否跟随重定向',
    body_type VARCHAR(32) NOT NULL DEFAULT 'none' COMMENT '请求体类型',
    headers_json JSON COMMENT '请求头JSON',
    query_params_json JSON COMMENT '查询参数JSON',
    cookies_json JSON COMMENT 'Cookie JSON',
    auth_json JSON COMMENT '认证JSON',
    body_json JSON COMMENT 'JSON请求体',
    form_fields_json JSON COMMENT '表单字段JSON',
    body_raw LONGBLOB COMMENT '原始请求体',
    body_mime VARCHAR(128) COMMENT '请求体MIME',
    graphql_query LONGTEXT COMMENT 'GraphQL查询',
    graphql_variables_json JSON COMMENT 'GraphQL变量JSON',
    soap_action VARCHAR(256) COMMENT 'SOAPAction',
    note VARCHAR(256) COMMENT '备注',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    CONSTRAINT fk_request_versions_request
        FOREIGN KEY (request_id) REFERENCES requests(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_request_versions_folder
        FOREIGN KEY (folder_id) REFERENCES folders(id)
        ON DELETE SET NULL,
    UNIQUE KEY uk_request_versions (request_id, version),
    KEY idx_request_versions_folder (folder_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='请求历史版本';

CREATE TABLE request_files (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    request_version_id BIGINT NOT NULL COMMENT '请求版本ID',
    field_name VARCHAR(128) NOT NULL COMMENT '表单字段名',
    file_name VARCHAR(255) NOT NULL COMMENT '文件名',
    content_type VARCHAR(128) COMMENT '内容类型',
    size BIGINT NOT NULL DEFAULT 0 COMMENT '文件大小',
    content LONGBLOB NOT NULL COMMENT '文件内容',
    enabled TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序号',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    CONSTRAINT fk_request_files_version
        FOREIGN KEY (request_version_id) REFERENCES request_versions(id)
        ON DELETE CASCADE,
    KEY idx_request_files_version (request_version_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='请求附件';

CREATE TABLE request_runs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    request_version_id BIGINT NOT NULL COMMENT '请求版本ID',
    environment_id BIGINT COMMENT '环境ID',
    status_code INT COMMENT '响应状态码',
    duration_ms INT COMMENT '耗时毫秒',
    success TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否成功',
    response_headers_json JSON COMMENT '响应头JSON',
    response_body LONGBLOB COMMENT '响应体',
    response_mime VARCHAR(128) COMMENT '响应体MIME',
    response_charset VARCHAR(64) COMMENT '响应字符集',
    response_size BIGINT COMMENT '响应体大小',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    CONSTRAINT fk_request_runs_version
        FOREIGN KEY (request_version_id) REFERENCES request_versions(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_request_runs_environment
        FOREIGN KEY (environment_id) REFERENCES environments(id)
        ON DELETE SET NULL,
    KEY idx_request_runs_version (request_version_id),
    KEY idx_request_runs_environment (environment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='请求执行记录';
