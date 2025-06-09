package com.pawsql.exception;

import java.util.HashMap;
import java.util.Map;

public class ErrorMessageResolver {
    private static final Map<String, String> errorMessages = new HashMap<>();

    static {
        // 初始化错误消息映射
        errorMessages.put("unknown.error", "发生了一个未知错误，请稍后重试");
        errorMessages.put("api.key.not.configured", "ApiKey未配置");
        errorMessages.put("license.code.not.exist", "Apikey不存在，请联系PawSQL管理员");
        errorMessages.put("license.code.not.valid", "许可证代码无效，请联系PawSQL管理员获取");
        errorMessages.put("beta.analysis.over.limit", "β用户优化次数超过限制");
        errorMessages.put("analysis.over.limit", "优化次数超过限制");
        errorMessages.put("plan.user.exceeded.maximum.opt.times", "优化次数已超出当前计划限制。如需继续使用，请升级您的计划。");
        errorMessages.put("workspace.not.exist", "工作空间不存在");
        errorMessages.put("workspace.has.no.analysis", "工作空间优化列表为空");
        errorMessages.put("analysis.not.exist", "优化不存在");
        errorMessages.put("error.create.analysis.failed", "SQL优化失败");
        errorMessages.put("error.login.invalidCredentials", "PawSQL权限验证错误, 请确认你的用户名密码信息输入无误。");
        errorMessages.put("error.backendUrl.invalid", "请检查您的PawSQL服务器地址是否输入正确或服务器是否运行");
        errorMessages.put("error.frontendUrl.invalid", "请检查 frontendUrl 是否正确配置或服务器是否运行");
        errorMessages.put("error.config.validate.failed", "验证失败");
        errorMessages.put("error.load.data.failed", "加载数据失败");
        errorMessages.put("nonexistent", "邮箱不存在");
        errorMessages.put("unactive", "很抱歉，您的邮箱尚未激活，请登录您的注册邮箱完成激活。");
    }

    public static String resolveErrorMessage(String code) {
        return errorMessages.getOrDefault(code, "发生了一个未知错误，请稍后重试");
    }

    public static String resolveErrorMessage(ErrorCode errorCode) {
        return resolveErrorMessage(errorCode.getMessageKey());
    }
}
