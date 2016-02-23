package rhedox.gesahuvertretungsplan.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAdView;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeContentAd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.model.Substitute;
import rhedox.gesahuvertretungsplan.model.SubstitutesList;
import rhedox.gesahuvertretungsplan.ui.fragment.MainFragment;
import rhedox.gesahuvertretungsplan.ui.viewHolders.ContentAdViewHolder;
import rhedox.gesahuvertretungsplan.ui.viewHolders.InstallAppAdViewHolder;
import rhedox.gesahuvertretungsplan.ui.viewHolders.NativeAdViewHolder;
import rhedox.gesahuvertretungsplan.ui.viewHolders.SubstituteViewHolder;

/**
 * Created by Robin on 28.10.2014.
 */
public class AdSubstitutesAdapter extends SelectableAdapter<Substitute, RecyclerView.ViewHolder> {
    private List list;

    @ColorInt private int circleColorImportant;
    @ColorInt private int circleColor;
    @ColorInt private int textColor;
    @ColorInt private int highlightedTextColor;
    @ColorInt private int activatedBackgroundColor;
    private int selected = -1;
    private int adCount = 0;
    private SubstituteViewHolder selectedViewHolder;

    private MainFragment.MaterialActivity activity;
    private Context context;

    private static final int ITEM_TYPE_SUBSTITUTE = 0;
    private static final int ITEM_TYPE_CONTENT_AD = 1;
    private static final int ITEM_TYPE_INSTALL_AD = 2;

    public AdSubstitutesAdapter(@NonNull Activity context) {
        this.list = new ArrayList(0);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(new int[]{R.attr.circleColor, R.attr.circleHighlightedColor, R.attr.circleTextColor, R.attr.circleHighlightedTextColor, R.attr.activatedColor});
        textColor = typedArray.getColor(2, 0);
        highlightedTextColor = typedArray.getColor(3, 0);
        circleColorImportant= typedArray.getColor(1, 0);
        circleColor = typedArray.getColor(0, 0);
        activatedBackgroundColor = typedArray.getColor(4,0);
        typedArray.recycle();

        if(context instanceof MainFragment.MaterialActivity)
            activity = (MainFragment.MaterialActivity)context;

        this.context = context.getApplicationContext();
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if(list == null || list.size() <= i)
            return;

        if(getItemViewType(i) == ITEM_TYPE_SUBSTITUTE) {
            SubstituteViewHolder substituteViewHolder = (SubstituteViewHolder) viewHolder;
            substituteViewHolder.setSubstitute((Substitute) list.get(i));
            substituteViewHolder.setSelected(i == selected && selected != -1);

            if(i == selected && selected != -1)
                selectedViewHolder = substituteViewHolder;
            else if(selectedViewHolder == substituteViewHolder)
                selectedViewHolder = null;
        }
        else {
            NativeAdViewHolder adViewHolder = (NativeAdViewHolder) viewHolder;
            adViewHolder.setAd((NativeAd) list.get(i));
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == ITEM_TYPE_SUBSTITUTE) {
            FrameLayout view = (FrameLayout) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_substitute, viewGroup, false);
            return new SubstituteViewHolder(view, this, circleColor, circleColorImportant, textColor, highlightedTextColor, activatedBackgroundColor);
        } else if (viewType == ITEM_TYPE_INSTALL_AD) {
            NativeAdView view = (NativeAdView) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_install_ad, viewGroup, false);
            return new InstallAppAdViewHolder(view);
        } else {
            NativeAdView view = (NativeAdView) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_content_ad, viewGroup, false);
            return new ContentAdViewHolder(view);
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(list == null || list.size() <= position)
            return -1;

        if(list.get(position) instanceof NativeContentAd)
            return ITEM_TYPE_CONTENT_AD;
        else if(list.get(position) instanceof NativeAppInstallAd)
            return ITEM_TYPE_INSTALL_AD;
        else
        return ITEM_TYPE_SUBSTITUTE;
    }

    public void setList(@Nullable List<Substitute> list, boolean filterImportant, boolean sortImportant) {
        if(filterImportant && sortImportant) {
            Log.e("Adapter", "Can't both filter and sort!");
            return;
        }

        if(list == null || list.size() == 0)
            clear();
        else {
            int count = getItemCount();

            if(sortImportant)
                this.list = Collections.unmodifiableList(SubstitutesList.sort(list));
            else if (filterImportant)
                this.list = Collections.unmodifiableList(SubstitutesList.filterImportant(list));

            if(count != list.size()) {
                if (count > list.size())
                    notifyItemRangeRemoved(Math.max(list.size() - 1, 0), count - list.size());
                else
                    notifyItemRangeInserted(Math.max(count - 1, 0), list.size() - count);
            }

            notifyItemRangeChanged(0, Math.min(list.size(), count));

            if(selected >= list.size())
                clearSelection(false);

            insertAds();
        }
    }
    public void clear() {
        clearSelection(false);
        if(getItemCount() > 0) {
            int count = getItemCount();
            list.clear();
            notifyItemRangeRemoved(0, count);
        }
    }

    private void insertAds() {
        int size = list.size();
        for (int i = 0; i < size / 6; i++) {
            AdLoader loader = new AdLoader.Builder(context, "ca-app-pub-3940256099942544/2247696110")
                    .forAppInstallAd(new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
                        @Override
                        public void onAppInstallAdLoaded(NativeAppInstallAd nativeAppInstallAd) {
                            if (list != null && list.size() >= (adCount + 1) * 5) {
                                Log.i("Ads", "ContentAd loaded");
                                list.add((adCount + 1) * 6, nativeAppInstallAd);
                                notifyItemInserted((adCount + 1) * 5);

                                adCount++;
                                Log.i("Ads", "Ad count: "+Integer.toString(adCount));
                            }
                        }
                    })
                    .forContentAd(new NativeContentAd.OnContentAdLoadedListener() {
                        @Override
                        public void onContentAdLoaded(NativeContentAd nativeContentAd) {
                            if (list != null && list.size() >= (adCount + 1) * 5) {
                                Log.i("Ads", "ContentAd loaded");
                                list.add((adCount + 1) * 5, nativeContentAd);
                                notifyItemInserted((adCount + 1) * 5);

                                adCount++;
                                Log.i("Ads", "Ad count: "+Integer.toString(adCount));
                            }
                        }
                    })
                    .build();

            AdRequest request = new AdRequest.Builder().build();
            loader.loadAd(request);
        }
    }

    @Override
    public void setSelected(RecyclerView.ViewHolder viewHolder) {
        if(viewHolder == null || selected == viewHolder.getAdapterPosition() || !(viewHolder instanceof SubstituteViewHolder))
            return;

        if(selectedViewHolder != null)
            selectedViewHolder.setSelected(false);

        selected = viewHolder.getAdapterPosition();
        selectedViewHolder = (SubstituteViewHolder)viewHolder;

        if(activity != null) {
            activity.setCabVisibility(true);
        }

    }

    @Override
    public void clearSelection(boolean cabFinished) {
        if(selectedViewHolder != null)
            selectedViewHolder.setSelected(false);

        selected = -1;
        selectedViewHolder = null;

        if(activity != null && !cabFinished)
                activity.setCabVisibility(false);
    }

    @Override
    public int getSelectedIndex() {
        return selected;
    }

    @Override
    public Substitute getSelected() {
        if(list == null || selected == -1) return null;
        if(!(list.get(selected) instanceof Substitute)) return null;

        return (Substitute) list.get(selected);
    }
}
