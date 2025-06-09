package com.pawsql.client;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.CredentialAttributesKt;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.RoamingType;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@State(
        name = "PawSQLSettings",
        storages = {
                @Storage(value = "pawsql-settings.xml", roamingType = RoamingType.DISABLED)
        }
)
public class PawSettingState implements PersistentStateComponent<PawSettingState> {

    private static final String CREDENTIAL_SUBSYSTEM = "PawSQL";
    private static final Logger LOG = Logger.getInstance(PawSettingState.class);

    private String baseUrl = "https://www.pawsql.com";
    private String frontendUrl = "";
    private String email;
    private boolean communityEdition = false;
    private String userKey;
    private boolean validate = true;
    private boolean analyze = true;
    private boolean rewrite = true;
    private int lang = 0;

    private boolean dedupIndex = true;
    private boolean indexOnly = true;
    private int maxMembers4IndexOnly = 4;
    private int maxMembers = 6;
    private int maxPerTable = 5;
    private String passwd;

    public boolean isUpdateStats() {
        return updateStats;
    }

    public void setUpdateStats(boolean updateStats) {
        this.updateStats = updateStats;
    }

    private boolean updateStats = true;

    public boolean isValidate() {
        return validate;
    }

    public void setValidate(boolean validate) {
        this.validate = validate;
    }

    public boolean isIndexOnly() {
        return indexOnly;
    }

    public void setIndexOnly(boolean indexOnly) {
        this.indexOnly = indexOnly;
    }

    public int getMaxMembers4IndexOnly() {
        return maxMembers4IndexOnly;
    }

    public void setMaxMembers4IndexOnly(int maxMembers4IndexOnly) {
        this.maxMembers4IndexOnly = maxMembers4IndexOnly;
    }

    public int getMaxMembers() {
        return maxMembers;
    }

    public void setMaxMembers(int maxMembers) {
        this.maxMembers = maxMembers;
    }

    public int getMaxPerTable() {
        return maxPerTable;
    }

    public void setMaxPerTable(int maxPerTable) {
        this.maxPerTable = maxPerTable;
    }

    public boolean isAnalyze() {
        return analyze;
    }

    public void setAnalyze(boolean analyze) {
        this.analyze = analyze;
    }

    public boolean isRewrite() {
        return rewrite;
    }

    public void setRewrite(boolean rewrite) {
        this.rewrite = rewrite;
    }

    public boolean isDedupIndex() {
        return dedupIndex;
    }

    public void setDedupIndex(boolean dedupIndex) {
        this.dedupIndex = dedupIndex;
    }


    public Map<String, String> loadConfig(PawSettingState settings) {
        Map<String, String> config = new HashMap<>();
        config.put("validate", String.valueOf(settings.isValidate()));
        config.put("rewrite", String.valueOf(settings.isRewrite()));
        config.put("analyze", String.valueOf(settings.isAnalyze()));
        config.put("lang", String.valueOf(settings.getLang()));
        config.put("dupindex", String.valueOf(settings.isDedupIndex()));
        config.put("indexOnly", String.valueOf(settings.isIndexOnly()));
        config.put("maxMembers4IndexOnly", String.valueOf(settings.getMaxMembers4IndexOnly()));
        config.put("maxMember", String.valueOf(settings.getMaxMembers()));
        config.put("maxPerTable", String.valueOf(settings.getMaxPerTable()));
        config.put("updateStats", String.valueOf(settings.isUpdateStats()));
        config.put("communityEdition", String.valueOf(settings.isCommunityEdition()));
        return config;
    }

    public int getLang() {
        return lang;
    }

    public void setLang(int lang) {
        this.lang = lang;
    }

    @NotNull
    public static PawSettingState getInstance() {
        PawSettingState state = ApplicationManager.getApplication().getService(PawSettingState.class);
        if (state == null) {
            LOG.error("Failed to get PawSettingState instance");
            state = new PawSettingState();
            state.noStateLoaded();
        }
        return state;
    }

    @Nullable
    @Override
    public PawSettingState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull PawSettingState state) {
        try {
            XmlSerializerUtil.copyBean(state, this);
            LOG.info("State loaded successfully");
        } catch (Exception e) {
            LOG.error("Error loading state", e);
            noStateLoaded();
        }
    }

    @Override
    public void noStateLoaded() {
        try {
            LOG.info("No state loaded, initializing default values");
            baseUrl = "https://www.pawsql.com";
            email = null;
            userKey = null;

            // 清除存储的密码
            CredentialAttributes attributes = createCredentialAttributes();
            PasswordSafe.getInstance().set(attributes, null);
        } catch (Exception e) {
            LOG.error("Error initializing default state", e);
        }
    }

    // Setters with notification
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setFrontendUrl(String frontendUrl) {
        this.frontendUrl = frontendUrl;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public void setPassword(String password) {
        CredentialAttributes attributes = createCredentialAttributes();
        Credentials credentials = new Credentials(email, password);
        PasswordSafe.getInstance().set(attributes, credentials);
    }

    // Getters remain unchanged
    public String getBaseUrl() {
        return baseUrl;
    }

    public String getFrontendUrl() {
        return frontendUrl;
    }

    public String getEmail() {
        return email;
    }

    public String getUserKey() {
        return userKey;
    }

    public String getPassword() {
        CredentialAttributes attributes = createCredentialAttributes();
        Credentials credentials = PasswordSafe.getInstance().get(attributes);
        return credentials != null ? credentials.getPasswordAsString() : null;
    }

    private static CredentialAttributes createCredentialAttributes() {
        return new CredentialAttributes(
                CredentialAttributesKt.generateServiceName("PawSQL", PawSettingState.CREDENTIAL_SUBSYSTEM)
        );
    }

    public boolean isCommunityEdition() {
        return communityEdition;
    }

    public void setCommunityEdition(boolean communityEdition) {
        this.communityEdition = communityEdition;
    }
}
