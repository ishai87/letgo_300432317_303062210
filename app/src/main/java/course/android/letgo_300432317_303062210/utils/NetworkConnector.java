package course.android.letgo_300432317_303062210.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.util.LruCache;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import course.android.letgo_300432317_303062210.Classes.Product;
import course.android.letgo_300432317_303062210.Classes.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class NetworkConnector {

    private static NetworkConnector mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static Context mCtx;


    private final String HOST_URL =  "http://10.0.2.2:8080/"; //"http://192.168.1.103:8080/";
    private  final String BASE_URL = HOST_URL + "app_res";

    private int TIME_OUT = 10000;
//    public static  int GET_ALL_PRODUCTS_JSON_REQ = 0;
//    public static final int INSERT_USER_REQ = 1;
//   public static final int DELETE_USER_REQ = 2;
//
//    public static final int DELETE_PRODUCT_REQ = 7;
//    public static final int GET_PRODUCT_IMAGE_REQ = 4;
//
//    public static final int GET_PRODUCTS_OF_USER_JSON_REQ = 5;
//
//    public static final int INSERT_PRODUCT_REQ = 6;
//    public static  int GET_ALL_USERS_JSON_REQ = 3;

  public static final int INSERT_USER_REQ = 1;//working
  public static final int DELETE_USER_REQ = 2;//working
  public static final int GET_ALL_USERS_JSON_REQ = 3;//working
  public static final int GET_USER_JSON_REQ = 4; //working

  //products
  public static final int GET_ALL_PRODUCTS_JSON_REQ = 5; //working
  public static final int INSERT_PRODUCT_REQ = 6;//working
  public static final int DELETE_PRODUCT_REQ = 7;//working
  public static final int GET_PRODUCT_IMAGE_REQ = 8;//working
  public static final int GET_PRODUCTS_OF_USER_JSON_REQ = 9; //working
  public static final int GET_PRODUCT_JSON_REQ = 10; //working




    private static final String USER_NAME = "name";
    private static final String USER_ID = "userId";
    //private static final String TUMBLER_PASS = "tumbler_pass";

    private static final String RESOURCE_FAIL_TAG = "{\"result_code\":0}";
    private static final String RESOURCE_SUCCESS_TAG = "{\"result_code\":1}";

    private static final String PRODUCT_ID = "id";
    private static final String PRODUCT_TITLE = "title";
    private static final String PRODUCT_DESCRIPTION = "description";
    private static final String PRODUCT_LOCATION = "location";
    private static final String PRODUCT_CATEGORY = "category";
    private static final String PRODUCT_PRICE = "price";




    private static final String REQ = "req";
    private static int REQ_TIRES =  1;//DefaultRetryPolicy.DEFAULT_MAX_RETRIES;

    private NetworkConnector() {

    }

    public static synchronized NetworkConnector getInstance() {
        if (mInstance == null) {
            mInstance = new NetworkConnector();
        }
        return mInstance;
    }

    public void initialize(Context context){
        mCtx = context;
        mRequestQueue = getRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    private void addToRequestQueue(String query, final NetworkResListener listener) {

        String reqUrl = BASE_URL + "?" + query;
        notifyPreUpdateListener(listener);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, reqUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        notifyProductUpdateListener(response, ResStatus.SUCCESS, listener);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();

                        JSONObject err = null;
                        try {
                            err = new JSONObject(RESOURCE_FAIL_TAG);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        finally {
                            notifyProductUpdateListener(err, ResStatus.FAIL, listener);
                        }

                    }
                });

        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                TIME_OUT,
                REQ_TIRES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        getRequestQueue().add(jsObjRequest);
    }

    private void addImageRequestToQueue(String query, final NetworkResListener listener){

        String reqUrl = BASE_URL + "?" + query;

        notifyPreUpdateListener(listener);

        getImageLoader().get(reqUrl, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                Bitmap bm = response.getBitmap();
                notifyProductBitmapUpdateListener(bm, ResStatus.SUCCESS, listener);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                notifyProductBitmapUpdateListener(null, ResStatus.FAIL, listener);
            }
        });
    }

    private ImageLoader getImageLoader() {
        return mImageLoader;
    }


    private void uploadProduct(final Product product, final NetworkResListener listener) {

        String reqUrl = HOST_URL + "upload_product?";
        notifyPreUpdateListener(listener);


        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, reqUrl,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            Toast.makeText(mCtx, obj.getString("result_code"), Toast.LENGTH_SHORT).show();
                            notifyProductUpdateListener(obj, ResStatus.SUCCESS, listener);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mCtx, error.getMessage(), Toast.LENGTH_SHORT).show();
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(RESOURCE_FAIL_TAG );
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        finally {
                            notifyProductUpdateListener(obj, ResStatus.FAIL, listener);
                        }

                    }
                }) {

            /*
            * If you want to add more parameters with the image
            * you can do it here
            * here we have only one parameter with the image
            * which is tags
            * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(PRODUCT_ID, product.getId());
                params.put(PRODUCT_TITLE, product.getTitle());
                params.put(PRODUCT_DESCRIPTION,  product.getDescription());
                params.put(PRODUCT_LOCATION,  product.getLocation());
                params.put(PRODUCT_CATEGORY, product.getCategory());
                params.put(PRODUCT_PRICE, product.getPrice());
                params.put(USER_ID, product.getUserId());
                return params;
            }


            /*
            * Here we are passing image by renaming it with a unique name
            * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                byte[] pic = Product.getImgAsByteArray(product.getImage1());
                params.put("fileField", new DataPart(imagename + ".png", pic));
                return params;
            }
        };
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                TIME_OUT,
                REQ_TIRES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //adding the request to volley
        getRequestQueue().add(volleyMultipartRequest);
    }

    private void uploadUser(final User user, final NetworkResListener listener) {

        String reqUrl = HOST_URL + "upload_user?";
        notifyPreUpdateListener(listener);


        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, reqUrl,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            Toast.makeText(mCtx, obj.getString("message"), Toast.LENGTH_SHORT).show();
                            notifyUserUpdateListener(obj, ResStatus.SUCCESS, listener);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mCtx, error.getMessage(), Toast.LENGTH_SHORT).show();
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(RESOURCE_FAIL_TAG );
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        finally {
                            notifyUserUpdateListener(obj, ResStatus.FAIL, listener);
                        }

                    }
                }) {

            /*
            * If you want to add more parameters with the image
            * you can do it here
            * here we have only one parameter with the image
            * which is tags
            * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(USER_NAME, user.getName());

                return params;
            }

        };
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                TIME_OUT,
                REQ_TIRES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //adding the request to volley
        getRequestQueue().add(volleyMultipartRequest);
    }


    public void sendRequestToServer(int requestCode, Product product, NetworkResListener listener){

        if(product==null){
            return;
        }

        Uri.Builder builder = new Uri.Builder();


        switch (requestCode){
            case INSERT_PRODUCT_REQ:{

                uploadProduct(product, listener);

                break;
            }

            case DELETE_PRODUCT_REQ:{
                builder.appendQueryParameter(REQ , String.valueOf(requestCode));
                builder.appendQueryParameter(PRODUCT_ID , product.getId());

                String query = builder.build().getEncodedQuery();
                addToRequestQueue(query, listener);

                break;
            }

            case GET_PRODUCT_IMAGE_REQ: {
                builder.appendQueryParameter(REQ , String.valueOf(requestCode));
                builder.appendQueryParameter(PRODUCT_ID , product.getId());

                String query = builder.build().getEncodedQuery();
                addImageRequestToQueue(query, listener);

                break;
            }
        }



    }


  public void sendImageRequestToServer(int requestCode, String productId, NetworkResListener listener){



    Uri.Builder builder = new Uri.Builder();


    switch (requestCode){

      case GET_PRODUCT_IMAGE_REQ: {
        builder.appendQueryParameter(REQ , String.valueOf(requestCode));
        builder.appendQueryParameter(PRODUCT_ID , productId);

        String query = builder.build().getEncodedQuery();
        addImageRequestToQueue(query, listener);

        break;
      }
    }



  }


  public void sendRequestToServerFolder(int requestCode, User user, NetworkResListener listener){

        if(user==null){
            return;
        }
        Uri.Builder builder = new Uri.Builder();

        switch (requestCode){
            case INSERT_USER_REQ:{
                uploadUser(user, listener);
                break;
            }

            case DELETE_USER_REQ:{
                builder.appendQueryParameter(REQ , String.valueOf(requestCode));
                builder.appendQueryParameter("name" , user.getName());

                String query = builder.build().getEncodedQuery();
                addToRequestQueue(query, listener);
                break;
            }
        }

    }


    public void updateProductsFeed(NetworkResListener listener){

        Uri.Builder builder = new Uri.Builder();
        builder.appendQueryParameter(REQ , String.valueOf(GET_ALL_PRODUCTS_JSON_REQ));
        String query = builder.build().getEncodedQuery();

        addToRequestQueue(query, listener);
    }

    public void updateUsersFeed(NetworkResListener listener){

        Uri.Builder builder = new Uri.Builder();
        builder.appendQueryParameter(REQ , String.valueOf(GET_ALL_USERS_JSON_REQ));
        String query = builder.build().getEncodedQuery();

        addToRequestQueue(query, listener);
    }


    private void clean() {

    }


    private  void notifyProductBitmapUpdateListener(final Bitmap res, final ResStatus status, final NetworkResListener listener) {

        Handler handler = new Handler(mCtx.getMainLooper());

        Runnable myRunnable = new Runnable() {

            @Override
            public void run() {
                try{
                    listener.onProductUpdate(res, status);
                }
                catch(Throwable t){
                    t.printStackTrace();
                }
            }
        };
        handler.post(myRunnable);

    }

    private  void notifyProductUpdateListener(final JSONObject res, final ResStatus status, final NetworkResListener listener) {

        Handler handler = new Handler(mCtx.getMainLooper());

        Runnable myRunnable = new Runnable() {

            @Override
            public void run() {
                try{
                    listener.onProductUpdate(res, status);
                }
                catch(Throwable t){
                    t.printStackTrace();
                }
            }
        };
        handler.post(myRunnable);

    }

    private  void notifyUserUpdateListener(final JSONObject res, final ResStatus status, final NetworkResListener listener) {

        Handler handler = new Handler(mCtx.getMainLooper());

        Runnable myRunnable = new Runnable() {

            @Override
            public void run() {
                try{
                    listener.onUserUpdate(res, status);
                }
                catch(Throwable t){
                    t.printStackTrace();
                }
            }
        };
        handler.post(myRunnable);

    }

    private  void notifyPreUpdateListener(final NetworkResListener listener) {


        Handler handler = new Handler(mCtx.getMainLooper());

        Runnable myRunnable = new Runnable() {

            @Override
            public void run() {
                try{
                    listener.onPreUpdate();
                }
                catch(Throwable t){
                    t.printStackTrace();
                }
            }
        };
        handler.post(myRunnable);

    }


}
