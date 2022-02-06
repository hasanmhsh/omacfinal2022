package hasan.mohamed.shehata.myapplication.templates;


import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class GeneralRecyclerViewItemViewHolder extends RecyclerView.ViewHolder {
    public View getItemView() {
        return itemView;
    }

    private View itemView;

    public GeneralRecyclerViewItemViewHolder(View itemView_) {
        super((View) itemView_);
        this.itemView = itemView_;
//        ((ViewGroup)itemView).setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    }

}

