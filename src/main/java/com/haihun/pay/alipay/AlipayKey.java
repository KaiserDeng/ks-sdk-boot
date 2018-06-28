package com.haihun.pay.alipay;

/**
 * 支付宝，公钥和私钥
 *
 * @author kaiser·von·d
 * @version 2018/5/2
 */
public class AlipayKey {

    /**
     * 支付宝应用公钥(支付宝提供)
     */
    // 正式
//    public static final String ALI_PAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgVqlXQYnRF95iqJ3A6QxKoqj5W245LYl/s1N3hlItjYW0RQfwx64ad70BjmqSlzFIzbylIASWcD1an0aRsLZTnbk+F5Jhaj8jQSZPzLEfaV5voB01l8TYLq5SNf9nxrmfN0voxBdH2pbtwKmNbTHosVSUeH6GCOpP03wAc4iapXqzSBBsELlc3Z+bU3sMVkNS8ebwG2qDQR8oIn4TymKW7DhNeqhrC6+hPA/epxsUpZr4wJehFYNEhvFCtHervMOcOzmb4EyB8p/QUeEbFBvGKZFX8rACJUyWEf0VI5cbzfkIdnUcO4h7BAuCseob/TpNvIxpk2DZPp4sNbnTnMnLwIDAQAB";

            // 沙箱
    //public static final String ALI_PAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4zXpwTY9/tyeoWzDMAhmDTJ8I4I1nq0Il1tgeCLc4Z290+55JiK9ibDXwntriTNoCds147Lr3xVfFnKpfeBPWZZtGB2ddqMmQ8iClS5iJ0XGBHuh6CJB0z/9jT3gG1Wg8JM7moq5SidV62NbxxN35UuS/M5Zq1ZY/bY8AWvX88ysCNTtU3pkA6yJ/ALZ1/fscaSqgR1WnBS4x6MVmKGk10w/Ip/Nw+AdM/AxNCYz/RM6U7REyz7nVVlLzTmhnPQjAELLrPbXyX1Nc0XD7WgRWg3YnF4Rko5k04SDXq0EegJW7hA+D9H2UtXnsKs0beVTgEXQhgdEXqBRpsW3sJQ4DQIDAQAB";

    // 应用2.0
    public static final String ALI_PAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgVqlXQYnRF95iqJ3A6QxKoqj5W245LYl/s1N3hlItjYW0RQfwx64ad70BjmqSlzFIzbylIASWcD1an0aRsLZTnbk+F5Jhaj8jQSZPzLEfaV5voB01l8TYLq5SNf9nxrmfN0voxBdH2pbtwKmNbTHosVSUeH6GCOpP03wAc4iapXqzSBBsELlc3Z+bU3sMVkNS8ebwG2qDQR8oIn4TymKW7DhNeqhrC6+hPA/epxsUpZr4wJehFYNEhvFCtHervMOcOzmb4EyB8p/QUeEbFBvGKZFX8rACJUyWEf0VI5cbzfkIdnUcO4h7BAuCseob/TpNvIxpk2DZPp4sNbnTnMnLwIDAQAB";



    /**
     * 商户应用私钥(自己提供)
     */
    //public static final String ALI_PAY_PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDKvYbVPb8tUggqgecC+JZGJRkvAZX9uRpt+vqGTTdMe/6Ie/wuxugCUN7aty86E87O0NcWOM/sElli8kVAltw3xOAH4agHgfEjL/RQHycv1O/8Gb2ve2kcEdJ9atNn1pqz+jqK01hpD8R6S1oa6uQxdOKqW9WIxRazFujZ+IeiuNGmy8aYYzH5X1fw5RFh2BKCKl4q4t+7zH7ZWL9nur1Prplaai/UZI4dwdp28qdntiC8sRHghhy7Bq75B+KDE6ot8WrdIHJTeyKrGhprOagdAk73nvLRHrCeqoSVg7DVZxb3OANBEBLwfAFvU07t6d29xBuNnmuFJLQlMKmOhqCnAgMBAAECggEAM9qJs9yZGwRSJl0XMsFV3hb6+/TR4hUH131LujHhpORJ06qyc1xYVs9MUHBt2dLbIDZ17K8XLu/6gL4qTDzJklH+YsWqblEj1bYcLKnrIea6gqYqx8kutwkv5vf9nh9bbhDgDEXF5QktXYIMR7owJx4YIy5dzR9rkN6ZK7bPKiTZ4lesJzURVUlRaSny+hfht77F/jMuGIE2As+E5pm7MsMA+XFcOZrGLfAn1XSUO9OO1TUxJetQXqZMB9Ixb0/9hCe41I8ero84BpVa/oaOPgoBsHDq3PtoA6DIulrD1ar2v+FiMSY2fMpqif7XcqfiIPfzmphHTss5XTCyISl00QKBgQD/DncN544MQTxdjI+Rx+UiPpC9YIIY9Z45EHAzP5EbSgyqSXMGn76hNvTx9m9ORXuOBEVq+nq6E2aB/rvfaCc6/SVtyfGXvmK03K8imMcbnY6VMs2X15Guxy6PAgDkrYTiH+cUhjZdgC913dPG9avwcUtSQzzjI2kRHmEBb7t4tQKBgQDLfYTabgq+/Xbn55jJnKeGbiw4CTEXPDfi8J/aqAvLETyWsSPK7Z13uA+fYe5Sj25tz1wRg+U62tcGTjtgVkBUXqBa2Ds8fCQ14YSh7jhRWmWWXCkPvknx8UCS5cbw7PrpQlhDJh6IZkwBb7GqGLDy0Hy/mW8YqUrcedgBh0uZawKBgFaNshkl2y6NdM7KRG5UR423babWeU+/ClKTIYBCshtwpZukEUTOMQLEg5sY5ezJ54PqLyR+m+dJbh/vMhI+ZhG+llULktDL6vM1cLls3ySf8yLuepZssSN+tG4TX/o6aD3UVbbBVPGl7nUxMgzykcamq8jeyGrMOXt1s7WpFRWBAoGAdVou2tVpZxm/kCAVEfhA+Jri73IFmJPG2x7P4YbWPkbzfSgtLyN3k4pXoCAXFA/ZrIRQYV2CPj3kkaNeRMGZGc70hU98YfZp35TuK4C5g9kxCtSdbZHdDSWHiu/umhTg4tK7GgaKxzjPjmh+ukzBB1dLCigPVNaU8K9n7gG7jH8CgYEA+Wrs0Uqot2UHdW8mVWg8n5T93+fU0wyBUpJt8eqIMPcONkLc6fJ+EK3pu4pPkXg0oWuPjnugiV2bcM+tiu+NyGVCxMPjDekd1VntnyqCzTPDfhEw7yyh2F9QF493Bx6yFTKuYS+tjL4KL3/0w+ps4KHYjczhewWVcWNsXBFPl48=";

            // 沙箱
    // public static final String ALI_PAY_PRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCRU0TCMDX3tlijSOJgdXxFJsVuHZxWekIrjiaAKm9S4U1w7r5fOiIZVZNe+vcWHbynohj727FfbvFiUxtPXn6RdQp9JoNQz56VUO0jnQhZR/xiAL/ZOM9kDDa+1ycsVl2LxBcnxtdufA5m/dVX028XE79uqDW89r4Gn4C8bgFHtm4e/qeqn8QJA/UUR1lQ6cfUCmTvsfRZ6SPL2qHHuDfLHJk0kQaMYVaBciC2sWsSQVuR7duQPYEi0pK+yrhx5h0ryDVGZ3PiohbrPZOVBo/A4SiTRqDlpIYZzLRMrU+NbU9v09sriU/3jmnLJSU+HHY6LFhfcDaWBM7WV6oct6nNAgMBAAECggEBAIWwJWPCuBLeI2JqzR3RVnXC+gKXqNeA9thcl5x17R1AeF44stalgvLtX3oTwzQMPR21J+ihrOVPCS3bqBb2pdan5fNFd63ioU9ZOB33+tMvPsTlTZfGBDtUZRRglv/Zn4rQ5EBKUqqn4xPp1NZLgjmniAaDJZRlJGCxYNt3EyRlEmMgVM26CsoxFuzIcc9m3Ew+z0VjRFeoVTiytWVM/sCPBeGFVXrbBD+LJryT7BEwwtGwpuPpaXtQFnb5eLgTbVVD3+ikLquEnJotSpSDMrixM1OjSZg0aU1ucWsP3GsC1NlPuTTChLKMszavV7wwfuPBksXJ0oCc2afMpPEEGZ0CgYEAybZ4Ws8Goe78YpbjPQNymhPsZew6YmbmMzusGXnSREQ8LpaN/2dPZCfQoD9vMiDkaB2gWbg6z8NEyz7o9NI//xqWCfrnDPvEiZQaitxeo72mr0Uj9tZrKpJ668j9yhaCncMA0HuR4FUUB7ogJF1Fm3r/Gum9085ah0l0ngnUyv8CgYEAuG/VYuH2wpHBn7RelW5F78ff4nPiIDGJ0jpmaYcWvopN5Q30L/iqU2y4W6XotrguGA8a4ItyHKJAPXjKffDg25tTDijShb01eUT41Px8ZJsFvFyzmW3gWs/IPZN9JX8Sm+q5J5XF17RxLaQeaCCAdNhWezcUYgw5GHPsETV5xzMCgYBoi5E/h5NpdIMM2iV2pwXLw82/Wbs86xYesavWHGSVi0Qh5Vg9ap8kStp/Y8FPGusm50wHlEuvCQbrzHRTB3Gx12ayT39XJFpN8Bv9oo/Oj+oHZ29nbORoUTkyYZ7gEeCqwbn3VERcH9xc0OP91PXRyuLTYRhI4efbvH+btX67owKBgGpZojPYlE9xeLK8cOW9pnozSLftlnB14EDnB/LFwKmyWmQ//ZRD0QM158CBFIV4AqxA2YCtJ+m27VAE/M5OPXqkP+okQ/B2QAVjbhByXtAsS8xnr/BC2WHD+nT3POyIL5TUbluTE3fkRnbI8fec9jVqIcP+6LP41FtzTKZHrabdAoGBAKGuNeoJ4QgX84yNLtRdADEvel+jM6FgWgmurxKgqRL2uhWY2XNeVPC4jkeUESOyuuaZGZ9hUOoppdAdV1dxfvAHdyL9jMR/txinbnbeLGt34B2wGutbhyk0aaFPLIWvTrDaxIClEmfr2dKsgl95AxQam0kLkoLx7Can60aC/WLt";

            // 应用2.0
    public static final String ALI_PAY_PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDPGkex/roFhQZXRkyo46sNwkDfmKZVPoBCj/TEf62mkUbuddwWtZpgWHeF2SQOx0PVfvF/v8eWDPiR4LR4L6fmXWyaIS3MROrFyIMjKDGtRI1vBlHoMjigAu6e4KPo00dAQmwoFJU5KPtsP6DULZTiLCXeqEHb7pJcodpsthCBoYcDbosT2c9Bb9/S9Kf5Pn3j3/D5gZ0CL1Y2Wv1r8Zinriy70Ew2ny/2oo4vDoi19Ydy9FG0E0Bh3lLUQI+gVjU2yszrjyDrbekehcSjofIT2VqCEHENJx+6FNzcLuahA2wRn+TDOboWAQGkhdqYi0OxBCB3Fpbbo0Q8plUwqo/TAgMBAAECggEAU+jo7YQwaHYaOJgiDTeZIYz37eASXG1jWBStbkPw3pDz9T/JeWYA81JEsvVkd5xiroitXj4cpn4A5FHvrNZaJ6roipYw0ReHK7Vu9Hqfjf09dKNR4kseyC4H8lU/8+BOeQmwepjEURF6tMQe7Hi/XtfWTB1N+cbyhBeLwQoPLAXDtLeEDbmvJxm7hu6qqr7dIXEiXGm+bbPvTrJVPZjwqtd/0/S++szRUOfrxPGJo7ocdvPxkAGOizMyzSwRDa2G4VpwWELR0UltDJe1mHc+Y7th4YdgRDh1fVsCLosnR+KsCxEYtpQJAwwx55atjXU9PLeSnM4AX6rUMGorI6m2IQKBgQD7p+2kLD1MuHkf6yLiWVHljFTJ/3Tabz1w7XeAP8w5fG1BYMtiVvv1xZpHblyU+TSjwci8AbxZHhomPe8tscvi2ku4fN5yjZ/SOE+5uhTv7McieyWVEEsAQ1YogXfXc9jjDP9DJ832v8/pg5N68jXVWKy73xhbdFyLGG4gW/cWIwKBgQDSrXhQYEC/RC92TwQN3IhbmaF2sZYbzsOvjvas4wujGir/qjmS59gZE7ibAe73CDKL49cEinGG2ym0W/E9R2CeXPwqkjw8Nv9N0wwD6UNcicJ2OPssjXgjTYPK0BKpOLKGCCeeTaiGcVbPiIjjmLGN/NH+3ex2N6DFvXOg9RZCkQKBgQDrM7RDsKVKQ0/RqS5dZyXH6TSF5qAMV9OQ7CC4WOhDAiwK4rC0JKAwwJLFGDynb5LnSoKWVPj4Qc4m19PnXTk1uNj0gKBXaE5rZ/37lBNlemNTss0ZWLUAde7cxMSBNeELtGYPGfDCIdvsX/FDA4WU9N/cZ1KYh1THG/oxfEKfCQKBgHkEqI4THF5CPrlJHl0QB0wG7hcOX9nNZd8c0lnxeifQyk5ebAAVKa8OAwcXjztb/B0bXdH/7MK4djZicirvK3MMmtWhdvRn6zO7PbWpcsZfY9MhXDb1dMNirYf9ChsmtHYFjSyEE4BW7rT8CxYiCiwzxDURKdmmjrXH4/nbj1PhAoGAIDUzrbdKvhygPZTiX94bFdFrYE+unVvOuJup6NtkExGflMH7bcGGPA1rmUBI9vHh3MyYeNYyhMz7GFzaPHoDfobsB4SEaBfXFhDp9EqVfNu1BjR//R4ZJJjlL9Jq/tmosps+tYEOd3/H6gf42DVRzw19ogzeH63rXb43liwV5k8=";

}
