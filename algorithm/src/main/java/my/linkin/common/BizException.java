package my.linkin.common;



public class BizException extends RuntimeException {

    private ResultCode resultCode;

    private String message;

    public ResultCode getResultCode() {
        return resultCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public BizException(ResultCode result) {
        this.resultCode = result;
    }

    public BizException(String message) {
        this.resultCode = ResultCode.ILLEGAL_ARGUMENTS;
        this.message = message;
    }

    public BizException(ResultCode result, String message) {
        super(message);
        this.resultCode = result;
        this.message = message;
    }
}
