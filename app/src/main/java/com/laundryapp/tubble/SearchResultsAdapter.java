package com.laundryapp.tubble;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.laundryapp.tubble.entities.LaundryShop;

import java.util.List;

/**
 * Created by Alagadnibabidy on 10/18/2016.
 */

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {

    private List<LaundryShop> laundryShopList;
    private SearchItemClick searchItemClickListener;

    // Provide a suitable constructor (depends on the kind of dataset)
    public SearchResultsAdapter(SearchItemClick searchItemClickListener) {
        this.searchItemClickListener = searchItemClickListener;
    }

    public void setData(List<LaundryShop> laundryShopList) {
        this.laundryShopList = laundryShopList;
    }
    // Create new views (invoked by the layout manager)
    @Override
    public SearchResultsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_result_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mNameView.setText(laundryShopList.get(position).getName());
        holder.mRatingView.setText(String.valueOf(laundryShopList.get(position).getRating()));
        holder.mAddressView.setText(laundryShopList.get(position).getAddress());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchItemClickListener.onItemClick(laundryShopList.get(position).getName());
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return laundryShopList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mNameView;
        public TextView mRatingView;
        public TextView mAddressView;

        public ViewHolder(View itemView) {
            super(itemView);
            mNameView = (TextView) itemView.findViewById(R.id.shop_name);
            mRatingView = (TextView) itemView.findViewById(R.id.rating);
            mAddressView = (TextView) itemView.findViewById(R.id.address);
        }
    }

    public interface SearchItemClick{
        void onItemClick(String name);
    }
}