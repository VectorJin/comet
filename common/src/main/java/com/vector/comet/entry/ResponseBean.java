package com.vector.comet.entry;

import com.vector.comet.constants.ErrorCode;

/**
 * Created by jinku on 2018/3/31.
 */
public class ResponseBean {

    private Integer errorCode;
    private String errorMsg;
    private String dataStr;

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getDataStr() {
        return dataStr;
    }

    public void setDataStr(String dataStr) {
        this.dataStr = dataStr;
    }

    @Override
    public String toString() {
        return "ResponseBean{" +
                "errorCode=" + errorCode +
                ", errorMsg='" + errorMsg + '\'' +
                ", dataStr='" + dataStr + '\'' +
                '}';
    }

    public static ResponseBean getErrorResponse(ErrorCode errorCode) {
        ResponseBean responseBean = new ResponseBean();
        responseBean.setErrorCode(errorCode.getCode());
        responseBean.setErrorMsg(errorCode.getDesc());
        return responseBean;
    }

    public static ResponseBean getFromResponse(String response) {
        ResponseBean responseBean = getErrorResponse(ErrorCode.SUCCESS);
        responseBean.setDataStr(response);
        return responseBean;
    }
}
