package hasan.mohamed.shehata.myapplication.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;

import androidx.databinding.DataBindingUtil;

import hasan.mohamed.shehata.myapplication.R;
import hasan.mohamed.shehata.myapplication.databinding.DualTextRvItemViewLayoutBinding;
import hasan.mohamed.shehata.myapplication.models.BindableItem;
import hasan.mohamed.shehata.myapplication.models.DownloadWindowContent;
import hasan.mohamed.shehata.myapplication.models.Group;
import hasan.mohamed.shehata.myapplication.models.ListItemBindableItemContentProvider;
import hasan.mohamed.shehata.myapplication.models.Message;
import hasan.mohamed.shehata.myapplication.models.User;
import hasan.mohamed.shehata.myapplication.types.ResultReceiver;

public class DualTextRecyclerViewItemView extends FrameLayout implements View.OnClickListener, BindableItem {
    private DualTextRvItemViewLayoutBinding binding;
    private ResultReceiver selectionReceiver;
    private boolean isMultipleChoices;
    public DualTextRecyclerViewItemView(Context context, ResultReceiver selectionReceiver, Boolean isMultipleChoices) {
        super(context);
        this.isMultipleChoices = isMultipleChoices;
        this.selectionReceiver = selectionReceiver;
        setWillNotDraw(false);
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li;
        li = (LayoutInflater)this.getContext().getSystemService(infService);
        this.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        //attach to parent must be true to be displayed
        binding = DataBindingUtil.inflate(li,R.layout.dual_text_rv_item_view_layout,this,true);
        View view = this.binding.getRoot();


        if(isMultipleChoices) {
            this.binding.dualTextRvItemContainer.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (binding.getDualtext() != null && binding.dualTextHighlitedSelectionContainer!=null) {
                        binding.getDualtext().toggleHighLight();
                        int visibility = binding.getDualtext().getIsHighLighted() ? View.VISIBLE : View.GONE;
                        binding.dualTextHighlitedSelectionContainer.setVisibility(visibility);
                    }
                }
            });
//            this.binding.dualTextRvItemContainer.setOnLongClickListener(new OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View view) {
//                    if (binding.getDualtext() != null && binding.dualTextHighlitedSelectionContainer!=null) {
//                        binding.getDualtext().toggleHighLight();
//                        int visibility = binding.getDualtext().getIsHighLighted() ? View.VISIBLE : View.GONE;
//                        binding.dualTextHighlitedSelectionContainer.setVisibility(visibility);
//                    }
//                    return true;
//                }
//            });
        }
        else{
            this.binding.dualTextRvItemContainer.setOnClickListener(this);
        }

        binding.isAdminGpsListItemChkbx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(binding != null && binding.getDualtext() != null) {
                    binding.getDualtext().setIsGroupAdmin(b);
                    binding.getDualtext().setIsHighLighted(true);
                    binding.dualTextHighlitedSelectionContainer.setVisibility(VISIBLE);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        selectionReceiver.receiveResult(binding.getDualtext());
    }

    @Override
    public void bind(ListItemBindableItemContentProvider bindableItemContentProvider) {
        if(binding == null || binding.isAdminGpsListItemChkbx == null)
            return;
        binding.setDualtext(bindableItemContentProvider);
        bindableItemContentProvider.drawLogo(binding.dualTextLogoIv);
        binding.isAdminGpsListItemChkbx.setChecked(binding.getDualtext().getIsGroupAdmin());
        if(!isMultipleChoices)
            binding.dualTextHighlitedSelectionContainer.setVisibility(binding.getDualtext().getHighlightedFilterVisibility());
        else
            binding.dualTextHighlitedSelectionContainer.setVisibility(GONE);
    }

    @Override
    public void bind(DownloadWindowContent downloadWindowContent) {
        throw new UnsupportedOperationException("This operation is not supported for this datatype please use ListItemBindableContentProvider as argument.");
    }

    @Override
    public void bind(User user) {
        bind((ListItemBindableItemContentProvider) user);
    }

    @Override
    public void bind(Group group) {

    }

    @Override
    public void bind(Message msg) {
        throw new UnsupportedOperationException("This operation is not supported for this datatype please use DownloadWindowContent as argument.");
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException();
    }
}
