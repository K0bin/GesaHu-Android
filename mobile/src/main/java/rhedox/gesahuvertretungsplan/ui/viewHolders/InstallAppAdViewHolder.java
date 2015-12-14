package rhedox.gesahuvertretungsplan.ui.viewHolders;

import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAdView;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeContentAdView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rhedox.gesahuvertretungsplan.R;

/**
 * Created by Robin on 21.11.2015.
 */


public class InstallAppAdViewHolder extends NativeAdViewHolder{

    @Bind(R.id.icon) ImageView icon;
    @Bind(R.id.title) AppCompatTextView title;
    @Bind(R.id.description) AppCompatTextView description;
    @Bind(R.id.callToAction) AppCompatButton callToAction;
    //@Bind(R.id.image) ImageView image;
    @Bind(R.id.store) AppCompatTextView store;
    @Bind(R.id.price) AppCompatTextView price;
    @Bind(R.id.rating) RatingBar rating;

    public InstallAppAdViewHolder(ViewGroup view) {
        super(view);

        NativeAppInstallAdView installAdView = (NativeAppInstallAdView) view;
        ButterKnife.bind(this, installAdView);

        installAdView.setCallToActionView(callToAction);
        installAdView.setBodyView(description);
        installAdView.setHeadlineView(title);
        installAdView.setIconView(icon);
        installAdView.setStoreView(store);
        installAdView.setPriceView(price);
        installAdView.setStarRatingView(rating);

        setAdView(installAdView);
    }

    @Override
    public void setAd(NativeAd ad) {
        NativeAppInstallAd installAd = (NativeAppInstallAd)ad;

        title.setText(installAd.getHeadline());
        icon.setImageDrawable(installAd.getIcon().getDrawable());
        store.setText(installAd.getStore());
        callToAction.setText(installAd.getCallToAction());
        description.setText(installAd.getBody());
        price.setText(installAd.getPrice());
        rating.setRating((float) (double) installAd.getStarRating());

        //if(installAd.getImages() != null && installAd.getImages().size() > 0)
        //    image.setImageDrawable(installAd.getImages().get(0).getDrawable());


        super.setAd(ad);
    }

    public void destroy() {
        ButterKnife.unbind(this);
    }
}
