package hasan.mohamed.shehata.myapplication.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
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
        this.binding.dualTextRvItemContainer.setOnClickListener(this);

        if(isMultipleChoices) {
            this.binding.dualTextRvItemContainer.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (binding.getDualtext() != null)
                        binding.getDualtext().toggleHighLight();
                    return true;
                }
            });
        }
        else{
            this.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        selectionReceiver.receiveResult(binding.getDualtext());
    }

    @Override
    public void bind(ListItemBindableItemContentProvider bindableItemContentProvider) {
        binding.setDualtext(bindableItemContentProvider);
        bindableItemContentProvider.drawLogo(binding.dualTextLogoIv);
    }

    @Override
    public void bind(DownloadWindowContent downloadWindowContent) {
        throw new UnsupportedOperationException("This operation is not supported for this datatype please use ListItemBindableContentProvider as argument.");
    }

    @Override
    public void bind(User user) {
        throw new UnsupportedOperationException("This operation is not supported for this datatype please use DownloadWindowContent as argument.");
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
