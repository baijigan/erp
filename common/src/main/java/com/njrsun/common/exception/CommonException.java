package com.njrsun.common.exception;

/**
 * @author njrsun
 * @create 2021/5/14 9:04
 */
public class CommonException extends  RuntimeException{
        private static final long serialVersionUID = 1L;

        protected final String message;

        public CommonException(String message)
        {
            this.message = message;
        }

        @Override
        public String getMessage()
        {
            return message;
        }

}
