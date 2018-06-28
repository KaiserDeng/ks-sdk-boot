package com.haihun.pay.unionpay;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 银联配置
 *
 * @author kaiser·von·d
 * @version 2018/5/16
 */
@ConfigurationProperties(prefix = "acpsdk")
@Component
public class UnionpayConfig {

    private String frontTransUrl;
    private String sina_idbackTransUrl;
    private String sina_idsingleQueryUrl;
    private String sina_idbatchTransUrl;
    private String sina_idfileTransUrl;
    private String sina_idappTransUrl;
    private String sina_idcardTransUrl;
    private String sina_idjfFrontTransUrl;
    private String sina_idjfBackTransUrl;
    private String sina_idjfSingleQueryUrl;
    private String sina_idjfCardTransUrl;
    private String sina_idjfAppTransUrl;
    private String sina_idversion;
    private String sina_idsignMethod;
    private String sina_idifValidateCNName;
    private String sina_idifValidateRemoteCert;
    private String sina_idbackUrl;
    private String sina_idfrontUrl;

    /**
     * 包含如下字段：<br/>
     * path,pwd,type
     */
    private Map<String, String> sina_idsignCert;

    /**
     * 包含如下字段：<br/>
     * path
     */
    private Map<String, String> sina_idencryptCert;

    /**
     * 包含如下字段：<br/>
     * dir
     */
    private Map<String, String> sina_idvalidateCert;


    public String getFrontTransUrl() {
        return frontTransUrl;
    }

    public void setFrontTransUrl(String frontTransUrl) {
        this.frontTransUrl = frontTransUrl;
    }

    public String getSina_idbackTransUrl() {
        return sina_idbackTransUrl;
    }

    public void setSina_idbackTransUrl(String sina_idbackTransUrl) {
        this.sina_idbackTransUrl = sina_idbackTransUrl;
    }

    public String getSina_idsingleQueryUrl() {
        return sina_idsingleQueryUrl;
    }

    public void setSina_idsingleQueryUrl(String sina_idsingleQueryUrl) {
        this.sina_idsingleQueryUrl = sina_idsingleQueryUrl;
    }

    public String getSina_idbatchTransUrl() {
        return sina_idbatchTransUrl;
    }

    public void setSina_idbatchTransUrl(String sina_idbatchTransUrl) {
        this.sina_idbatchTransUrl = sina_idbatchTransUrl;
    }

    public String getSina_idfileTransUrl() {
        return sina_idfileTransUrl;
    }

    public void setSina_idfileTransUrl(String sina_idfileTransUrl) {
        this.sina_idfileTransUrl = sina_idfileTransUrl;
    }

    public String getSina_idappTransUrl() {
        return sina_idappTransUrl;
    }

    public void setSina_idappTransUrl(String sina_idappTransUrl) {
        this.sina_idappTransUrl = sina_idappTransUrl;
    }

    public String getSina_idcardTransUrl() {
        return sina_idcardTransUrl;
    }

    public void setSina_idcardTransUrl(String sina_idcardTransUrl) {
        this.sina_idcardTransUrl = sina_idcardTransUrl;
    }

    public String getSina_idjfFrontTransUrl() {
        return sina_idjfFrontTransUrl;
    }

    public void setSina_idjfFrontTransUrl(String sina_idjfFrontTransUrl) {
        this.sina_idjfFrontTransUrl = sina_idjfFrontTransUrl;
    }

    public String getSina_idjfBackTransUrl() {
        return sina_idjfBackTransUrl;
    }

    public void setSina_idjfBackTransUrl(String sina_idjfBackTransUrl) {
        this.sina_idjfBackTransUrl = sina_idjfBackTransUrl;
    }

    public String getSina_idjfSingleQueryUrl() {
        return sina_idjfSingleQueryUrl;
    }

    public void setSina_idjfSingleQueryUrl(String sina_idjfSingleQueryUrl) {
        this.sina_idjfSingleQueryUrl = sina_idjfSingleQueryUrl;
    }

    public String getSina_idjfCardTransUrl() {
        return sina_idjfCardTransUrl;
    }

    public void setSina_idjfCardTransUrl(String sina_idjfCardTransUrl) {
        this.sina_idjfCardTransUrl = sina_idjfCardTransUrl;
    }

    public String getSina_idjfAppTransUrl() {
        return sina_idjfAppTransUrl;
    }

    public void setSina_idjfAppTransUrl(String sina_idjfAppTransUrl) {
        this.sina_idjfAppTransUrl = sina_idjfAppTransUrl;
    }

    public String getSina_idversion() {
        return sina_idversion;
    }

    public void setSina_idversion(String sina_idversion) {
        this.sina_idversion = sina_idversion;
    }

    public String getSina_idsignMethod() {
        return sina_idsignMethod;
    }

    public void setSina_idsignMethod(String sina_idsignMethod) {
        this.sina_idsignMethod = sina_idsignMethod;
    }

    public String getSina_idifValidateCNName() {
        return sina_idifValidateCNName;
    }

    public void setSina_idifValidateCNName(String sina_idifValidateCNName) {
        this.sina_idifValidateCNName = sina_idifValidateCNName;
    }

    public String getSina_idifValidateRemoteCert() {
        return sina_idifValidateRemoteCert;
    }

    public void setSina_idifValidateRemoteCert(String sina_idifValidateRemoteCert) {
        this.sina_idifValidateRemoteCert = sina_idifValidateRemoteCert;
    }

    public String getSina_idbackUrl() {
        return sina_idbackUrl;
    }

    public void setSina_idbackUrl(String sina_idbackUrl) {
        this.sina_idbackUrl = sina_idbackUrl;
    }

    public String getSina_idfrontUrl() {
        return sina_idfrontUrl;
    }

    public void setSina_idfrontUrl(String sina_idfrontUrl) {
        this.sina_idfrontUrl = sina_idfrontUrl;
    }

    public Map<String, String> getSina_idsignCert() {
        return sina_idsignCert;
    }

    public void setSina_idsignCert(Map<String, String> sina_idsignCert) {
        this.sina_idsignCert = sina_idsignCert;
    }

    public Map<String, String> getSina_idencryptCert() {
        return sina_idencryptCert;
    }

    public void setSina_idencryptCert(Map<String, String> sina_idencryptCert) {
        this.sina_idencryptCert = sina_idencryptCert;
    }

    public Map<String, String> getSina_idvalidateCert() {
        return sina_idvalidateCert;
    }

    public void setSina_idvalidateCert(Map<String, String> sina_idvalidateCert) {
        this.sina_idvalidateCert = sina_idvalidateCert;
    }
}
