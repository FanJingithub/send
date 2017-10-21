package com.fudan.helper;

import android.os.AsyncTask;
import android.os.Handler;


import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by FanJin on 2017/10/09.
 * HttpConnector can help us to connect our server
 */

public class HttpConnector {

    /**
     * HttpTask is an AsyncTask which we can connect the server on another thread.
     * the task could be cancelled if time out.
     * it will trigger a listener when the task stops.
     * how to trigger the listener: send value -1 if getting exception or time out , or send value 1 if successful.
     */
    static class HttpTask extends AsyncTask<Void,Void,Void>
    {
        HttpListener mListener;
        Request request;
        /**
         * flag : refresh the UI if true
         * I am so tired now, so this function will be finished later.
         */
        boolean flag;
        boolean done = false;
        String responseData = "";


        public HttpTask(HttpListener listener,Request request,boolean flag){
            this.mListener = listener;
            this.request = request;
            this.flag = flag;
        }

        protected void onPreExecute() {
            if (flag){
                //
            }
        }

        protected Void doInBackground(Void... params) {
            OkHttpClient client = new OkHttpClient();
            try {
                Response response = client.newCall(request).execute();
                responseData = response.body().string();
                done = true;
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            if (flag){

            }
            if (! done){
                mListener.onHttpFinish(-1,"");
            }else {
                mListener.onHttpFinish(1,responseData);
            }
        }

        protected void onCancelled() {
            super.onCancelled();
            mListener.onHttpFinish(-1,"");
        }
    }

    /**
     * execute the task. the task would be cancelled if time out
     * @param s : a HttpTask
     */
    private static void excuteTask(final HttpTask s){
        s.execute();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (! s.done){
                    s.cancel(true);
                }
            }
        },2000);
    }

    /**
     * use POST to login
     */
    public static void login(String num,String name, String pwd, HttpListener listener ){
        RequestBody requestBody = new FormBody.Builder()
                .add("num",num)
                .add("name",name)
                .add("pwd",pwd)
                .build();
        Request request = new Request.Builder()
                .url(formatURL("login"))
                .post(requestBody)
                .build();
        final HttpTask s = new HttpTask(listener,request,true);
        excuteTask(s);
    }

    /**
     * use POST to trigger the verification system.
     */
    public static void getRatify(String num, HttpListener listener){
        RequestBody requestBody = new FormBody.Builder()
                .add("num",num)
                .build();
        Request request = new Request.Builder()
                .url(formatURL("ratify"))
                .post(requestBody)
                .build();
        final HttpTask s = new HttpTask(listener,request,true);
        excuteTask(s);
    }

    /**
     * use POST to send feedback message.
     */
    public static void myFeedback(String num,String inf, HttpListener listener){
        RequestBody requestBody = new FormBody.Builder()
                .add("num",num)
                .add("inf",inf)
                .build();
        Request request = new Request.Builder()
                .url(formatURL("feedback"))
                .post(requestBody)
                .build();
        final HttpTask s = new HttpTask(listener,request,true);
        excuteTask(s);
    }

    /**
     * use GET to check new version.
     */
    public static void checkNew(int i,HttpListener listener){
        Request request = new Request.Builder()
                .url(formatURL("checkNew?version="+i))
                .build();
        final HttpTask s = new HttpTask(listener,request,true);
        excuteTask(s);
    }

    /**
     * use GET to download the new version.
     */
    public static void downloadNew(HttpListener listener){
        Request request = new Request.Builder()
                .url(formatURL("downloadNew"))
                .build();
        final HttpTask s = new HttpTask(listener,request,false);
        excuteTask(s);
    }

    /**
     * use POST to send data of location and sos.
     */
    public static void sendLocation(int type, String num, String fornum, int sos, double lat, double lng, HttpListener listener ) {
        RequestBody requestBody = new FormBody.Builder()
                .add("type",type+"")
                .add("num",num)
                .add("fornum",fornum)
                .add("sos",sos+"")
                .add("latitude",lat+"")
                .add("longitude",lng+"")
                .build();
        Request request = new Request.Builder()
                .url(formatURL("UpLocation"))
                .post(requestBody)
                .build();
        final HttpTask s = new HttpTask(listener,request,false);
        excuteTask(s);
    }

    /**
     * use POST to trigger the verification system.
     */
    public static void setMessage(String num, String message,HttpListener listener){
        RequestBody requestBody = new FormBody.Builder()
                .add("num",num)
                .add("message",message)
                .build();
        Request request = new Request.Builder()
                .url(formatURL("setMessage"))
                .post(requestBody)
                .build();
        final HttpTask s = new HttpTask(listener,request,true);
        excuteTask(s);
    }

    public static void reportMyStateChanged(String num, int myState,HttpListener listener){
        RequestBody requestBody = new FormBody.Builder()
                .add("num",num)
                .add("myState",myState+"")
                .build();
        Request request = new Request.Builder()
                .url(formatURL("reportC"))
                .post(requestBody)
                .build();
        final HttpTask s = new HttpTask(listener,request,true);
        excuteTask(s);
    }

    /**
     * check whether being warned
     * @param num
     * @param listener
     */
    public static void checkWrong(String num,HttpListener listener){
        Request request = new Request.Builder()
                .url(formatURL("checkWrong?num="+num))
                .build();
        final HttpTask s = new HttpTask(listener,request,false);
        excuteTask(s);
    }

    static String formatURL(String command){
        //return "http://118.89.104.204:8888/" + command;
        //return "http://100.1.19.33:8888/" + command;
        //return "http://192.168.31.83:8888/" + command;
        return "http://118.89.111.214:8888/" + command;
    }
}
