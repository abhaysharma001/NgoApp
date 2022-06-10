package com.chetna.ngo.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.chetna.ngo.databinding.RvAddSliderLayoutBinding;
import com.chetna.ngo.interfaces.OpenBottomSheetDialog;
import com.chetna.ngo.models.SliderItem;
import com.chetna.ngo.utils.BaseUrls;
import com.chetna.ngo.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddSliderAdapter extends RecyclerView.Adapter<AddSliderAdapter.ViewHolder> {
    private ArrayList<SliderItem> sliderItemArrayList;
    private Context context;
    private OpenBottomSheetDialog openBottomSheetDialog;

    public AddSliderAdapter(ArrayList<SliderItem> sliderItemArrayList, Context context, OpenBottomSheetDialog openBottomSheetDialog) {
        this.sliderItemArrayList = sliderItemArrayList;
        this.context = context;
        this.openBottomSheetDialog=openBottomSheetDialog;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(RvAddSliderLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SliderItem sliderItem = sliderItemArrayList.get(position);
        holder.binding.sliderText.setText(sliderItem.getText());
        holder.binding.deleteSlider.setOnClickListener(v -> {
            deleteSlider(position);
        });
        holder.binding.editSlider.setOnClickListener(v -> {
        });
        Glide.with(holder.binding.getRoot())
                .load(BaseUrls.BASE_URL + sliderItem.getImage())
                .fitCenter()
                .into(holder.binding.sliderImage);

        holder.binding.editSlider.setOnClickListener(v->{
            if(openBottomSheetDialog!=null){
                openBottomSheetDialog.openBottomSheet(true,sliderItem);
            }
        });
    }

    private void deleteSlider(int position) {
        Constants.showProgressDialog("Deleting Slider!", "Please Wait...", context);
        StringRequest request = new StringRequest(Request.Method.POST, BaseUrls.ADD_SLIDER_IMAGE, response -> {
            Log.e("TAG", "changeMessage: " + response);
            try {
                Constants.hideProgressDialog();
                JSONObject jsonObject = new JSONObject(response);
                Constants.showToast(context, jsonObject.getString("message"));
                if (jsonObject.has("status")) {
                    if (jsonObject.getBoolean("status")) {
                        sliderItemArrayList.remove(position);
                        notifyItemRemoved(position);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Constants.hideProgressDialog();
            }

        }, error -> {
            Constants.hideProgressDialog();
            if (error instanceof NetworkError || error instanceof TimeoutError) {
                Constants.showToast(context, "Please Check your Internet connection");
                return;
            }
            Constants.showToast(context, error.getLocalizedMessage());
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("project_id", sliderItemArrayList.get(position).getProject_id());
                params.put("status", "3");
                params.put("id", sliderItemArrayList.get(position).getId());
                params.put("user_id", Constants.getString(context, Constants.USER_ID));
                Log.e("PARAMS", "getParams: " + params);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());
        request.setRetryPolicy(new
                DefaultRetryPolicy(
                Constants.CONNECTION_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

    @Override
    public int getItemCount() {
        return sliderItemArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private RvAddSliderLayoutBinding binding;

        public ViewHolder(@NonNull RvAddSliderLayoutBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
