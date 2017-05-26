package com.jgkj.bxxc.bean;

import java.util.List;

/**
 * Created by shijun on 2017/5/26.
 */

public class SubPicture {
    private int code;
    private String reason;
    private List<Result> result;
    private List<String> morepic;

    public int getCode() {
        return code;
    }

    public String getReason() {
        return reason;
    }

    public List<Result> getResult() {
        return result;
    }

    public List<String> getMorepic() {
        return morepic;
    }

    public class Result{
        private String pic;

        private int key;

        private String url;

        private String title;

        public String getPic() {
            return pic;
        }

        public int getKey() {
            return key;
        }

        public String getUrl() {
            return url;
        }

        public String getTitle() {
            return title;
        }
    }
}
