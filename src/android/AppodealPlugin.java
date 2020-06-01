package com.plugin.appodeal;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import android.widget.LinearLayout;
import android.widget.FrameLayout;
import android.view.ViewGroup;
import android.view.Gravity;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.AppodealUnityBannerView;
import com.appodeal.ads.BannerCallbacks;
import com.appodeal.ads.InterstitialCallbacks;
import com.appodeal.ads.RewardedVideoCallbacks;
import com.appodeal.ads.NonSkippableVideoCallbacks;
import com.appodeal.ads.UserSettings;
import com.appodeal.ads.BannerView;
import com.appodeal.ads.utils.Log;

import org.json.JSONObject;

public class AppodealPlugin extends CordovaPlugin {

    private static final String ACTION_IS_INITIALIZED = "isInitalized";
    private static final String ACTION_INITIALIZE = "initialize";
    private static final String ACTION_SHOW = "show";
    private static final String ACTION_SHOW_WITH_PLACEMENT = "showWithPlacement";
    private static final String ACTION_SHOW_BANNER_VIEW = "showBannerView";
    private static final String ACTION_IS_LOADED = "isLoaded";
    private static final String ACTION_CACHE = "cache";
    private static final String ACTION_HIDE = "hide";
    private static final String ACTION_DESTROY = "destroy";
    private static final String ACTION_SET_AUTO_CACHE = "setAutoCache";
    private static final String ACTION_IS_PRECACHE = "isPrecache";

    private static final String ACTION_BANNER_ANIMATION = "setBannerAnimation";
    private static final String ACTION_BANNER_BACKGROUND = "setBannerBackground";
    private static final String ACTION_SMART_BANNERS = "setSmartBanners";
    private static final String ACTION_728X90_BANNERS = "set728x90Banners";
    private static final String ACTION_BANNERS_OVERLAP = "setBannerOverLap";

    private static final String ACTION_SET_TESTING = "setTesting";
    private static final String ACTION_SET_LOGGING = "setLogLevel";
    private static final String ACTION_SET_CHILD_TREATMENT = "setChildDirectedTreatment";
    private static final String ACTION_DISABLE_NETWORK = "disableNetwork";
    private static final String ACTION_DISABLE_NETWORK_FOR_TYPE = "disableNetworkType";
    private static final String ACTION_DISABLE_LOCATION_PERMISSION_CHECK = "disableLocationPermissionCheck";
    private static final String ACTION_DISABLE_WRITE_EXTERNAL_STORAGE_CHECK = "disableWriteExternalStoragePermissionCheck";
    private static final String ACTION_SET_ON_LOADED_TRIGGER_BOTH = "setTriggerOnLoadedOnPrecache";
    private static final String ACTION_MUTE_VIDEOS_IF_CALLS_MUTED = "muteVideosIfCallsMuted";
    private static final String ACTION_START_TEST_ACTIVITY = "showTestScreen";
    private static final String ACTION_SET_PLUGIN_VERSION = "setPluginVersion";
    private static final String ACTION_GET_VERSION = "getVersion";

    private static final String ACTION_CAN_SHOW = "canShow";
    private static final String ACTION_CAN_SHOW_WITH_PLACEMENT = "canShowWithPlacement";
    private static final String ACTION_SET_CUSTOM_INTEGER_RULE = "setCustomIntegerRule";
    private static final String ACTION_SET_CUSTOM_BOOLEAN_RULE = "setCustomBooleanRule";
    private static final String ACTION_SET_CUSTOM_DOUBLE_RULE = "setCustomDoubleRule";
    private static final String ACTION_SET_CUSTOM_STRING_RULE = "setCustomStringRule";
    private static final String ACTION_GET_REWARD_PARAMETERS = "getRewardParameters";
    private static final String ACTION_GET_REWARD_PARAMETERS_FOR_PLACEMENT = "getRewardParametersForPlacement";

    private static final String ACTION_SET_AGE = "setAge";
    private static final String ACTION_SET_GENDER = "setGender";
    private static final String ACTION_SET_USER_ID = "setUserId";

    private static final String ACTION_SET_INTERSTITIAL_CALLBACKS = "setInterstitialCallbacks";
    private static final String ACTION_SET_NON_SKIPPABLE_VIDEO_CALLBACKS = "setNonSkippableVideoCallbacks";
    private static final String ACTION_SET_REWARDED_CALLBACKS = "setRewardedVideoCallbacks";
    private static final String ACTION_SET_BANNER_CALLBACKS = "setBannerCallbacks";

    private boolean isInitialized = false;
    private boolean bannerOverlap = true;
    private ViewGroup parentView;
    private BannerView bannerView;
    private UserSettings userSettings;

    private static final String CALLBACK_INIT = "onInit";
    private static final String CALLBACK_LOADED = "onLoaded";
    private static final String CALLBACK_FAILED = "onFailedToLoad";
    private static final String CALLBACK_CLICKED = "onClick";
    private static final String CALLBACK_SHOWN = "onShown";
    private static final String CALLBACK_CLOSED = "onClosed";
    private static final String CALLBACK_FINISHED = "onFinished";
    private static final String CALLBACK_EXPIRED = "onExpired";
    private static final String CALLBACK_SHOW_FAILED = "onShowFailed";
    private CallbackContext interstitialCallbacks;
    private CallbackContext bannerCallbacks;
    private CallbackContext nonSkippableCallbacks;
    private CallbackContext rewardedCallbacks;

    @Override
    public boolean execute(String action, JSONArray args,
                           final CallbackContext callback) throws JSONException {

        switch (action) {
            case ACTION_INITIALIZE: {
                final String appKey = args.getString(0);
                final int adType = args.getInt(1);
                final boolean consent = args.getBoolean(2);
                cordova.getActivity().runOnUiThread(() -> {
                    Appodeal.initialize(cordova.getActivity(), appKey, getAdType(adType), consent);
                    isInitialized = true;
                });
                return true;
            }
            case ACTION_IS_INITIALIZED:
                cordova.getActivity().runOnUiThread(() -> {
                    if (isInitialized) {
                        callback.sendPluginResult(new PluginResult(PluginResult.Status.OK, true));
                    } else {
                        callback.sendPluginResult(new PluginResult(PluginResult.Status.OK, false));
                    }
                });
                return true;
            case ACTION_SHOW: {
                final int adType = args.getInt(0);
                cordova.getActivity().runOnUiThread(() -> {
                    int rAdType = getAdType(adType);
                    boolean res;
                    if (rAdType == Appodeal.BANNER || rAdType == Appodeal.BANNER_BOTTOM || rAdType == Appodeal.BANNER_TOP) {
                        res = showBanner(adType, null);
                    } else {
                        res = Appodeal.show(cordova.getActivity(), getAdType(adType));
                    }
                    callback.sendPluginResult(new PluginResult(PluginResult.Status.OK, res));
                });
                return true;
            }
            case ACTION_SHOW_WITH_PLACEMENT: {
                final int adType = args.getInt(0);
                final String placement = args.getString(1);
                cordova.getActivity().runOnUiThread(() -> {
                    int rAdType = getAdType(adType);
                    boolean res;
                    if (rAdType == Appodeal.BANNER || rAdType == Appodeal.BANNER_BOTTOM || rAdType == Appodeal.BANNER_TOP) {
                        res = showBanner(adType, placement);
                    } else {
                        res = Appodeal.show(cordova.getActivity(), getAdType(adType), placement);
                    }
                    callback.sendPluginResult(new PluginResult(PluginResult.Status.OK, res));
                });
                return true;
            }
            case ACTION_SHOW_BANNER_VIEW: {
                final int xAxis = args.getInt(0);
                final int yAxis = args.getInt(1);
                final String placement = args.getString(2);
                cordova.getActivity().runOnUiThread(() -> {
                    if (placement != null) {
                        AppodealUnityBannerView.getInstance().showBannerView(cordova.getActivity(), xAxis, yAxis, placement);
                    } else {
                        AppodealUnityBannerView.getInstance().showBannerView(cordova.getActivity(), xAxis, yAxis, "");
                    }
                });
                return true;
            }
            case ACTION_IS_LOADED: {
                final int adType = args.getInt(0);
                cordova.getActivity().runOnUiThread(() -> {
                    if (Appodeal.isLoaded(getAdType(adType))) {
                        callback.sendPluginResult(new PluginResult(PluginResult.Status.OK, true));
                    } else {
                        callback.sendPluginResult(new PluginResult(PluginResult.Status.OK, false));
                    }
                });
                return true;
            }
            case ACTION_CACHE: {
                final int adType = args.getInt(0);
                cordova.getActivity().runOnUiThread(() -> Appodeal.cache(cordova.getActivity(), getAdType(adType)));
                return true;
            }
            case ACTION_HIDE: {
                final int adType = args.getInt(0);
                cordova.getActivity().runOnUiThread(() -> {
                    Appodeal.hide(cordova.getActivity(), getAdType(adType));
                    AppodealUnityBannerView.getInstance().hideBannerView(cordova.getActivity());
                });
                return true;
            }
            case ACTION_DESTROY: {
                final int adType = args.getInt(0);
                cordova.getActivity().runOnUiThread(() -> Appodeal.destroy(adType));
                return true;
            }
            case ACTION_SET_AUTO_CACHE: {
                final int adType = args.getInt(0);
                final boolean autoCache = args.getBoolean(1);
                cordova.getActivity().runOnUiThread(() -> Appodeal.setAutoCache(getAdType(adType), autoCache));
                return true;
            }
            case ACTION_IS_PRECACHE: {
                final int adType = args.getInt(0);
                cordova.getActivity().runOnUiThread(() -> {
                    if (Appodeal.isPrecache(getAdType(adType))) {
                        callback.sendPluginResult(new PluginResult(PluginResult.Status.OK, true));
                    } else {
                        callback.sendPluginResult(new PluginResult(PluginResult.Status.OK, false));
                    }
                });
                return true;
            }
            case ACTION_BANNER_ANIMATION: {
                final boolean value = args.getBoolean(0);
                cordova.getActivity().runOnUiThread(() -> Appodeal.setBannerAnimation(value));
                return true;
            }
            case ACTION_BANNER_BACKGROUND: {
                return true;
            }
            case ACTION_SMART_BANNERS: {
                final boolean value = args.getBoolean(0);
                cordova.getActivity().runOnUiThread(() -> Appodeal.setSmartBanners(value));
                return true;
            }
            case ACTION_728X90_BANNERS: {
                final boolean value = args.getBoolean(0);
                cordova.getActivity().runOnUiThread(() -> Appodeal.set728x90Banners(value));
                return true;
            }
            case ACTION_BANNERS_OVERLAP: {
                final boolean value = args.getBoolean(0);
                cordova.getActivity().runOnUiThread(() -> bannerOverlap = value);
                return true;
            }
            case ACTION_SET_TESTING:
                final boolean testing = args.getBoolean(0);
                cordova.getActivity().runOnUiThread(() -> Appodeal.setTesting(testing));
                return true;
            case ACTION_SET_LOGGING:
                final int logLevel = args.getInt(0);
                cordova.getActivity().runOnUiThread(() -> {
                    switch (logLevel) {
                        case 0:
                            Appodeal.setLogLevel(Log.LogLevel.none);
                            break;
                        case 1:
                            Appodeal.setLogLevel(Log.LogLevel.debug);
                            break;
                        case 2:
                            Appodeal.setLogLevel(Log.LogLevel.verbose);
                            break;
                        default:
                            break;
                    }
                });
                return true;
            case ACTION_SET_CHILD_TREATMENT: {
                final boolean value = args.getBoolean(0);
                cordova.getActivity().runOnUiThread(() -> Appodeal.setChildDirectedTreatment(value));
                return true;
            }
            case ACTION_DISABLE_NETWORK: {
                final String network = args.getString(0);
                cordova.getActivity().runOnUiThread(() -> Appodeal.disableNetwork(cordova.getActivity(), network));
                return true;
            }
            case ACTION_DISABLE_NETWORK_FOR_TYPE: {
                final String network = args.getString(0);
                final int adType = args.getInt(1);
                cordova.getActivity().runOnUiThread(() -> Appodeal.disableNetwork(cordova.getActivity(), network, getAdType(adType)));
                return true;
            }
            case ACTION_SET_ON_LOADED_TRIGGER_BOTH: {
                final int adType = args.getInt(0);
                final boolean setOnTriggerBoth = args.getBoolean(1);
                cordova.getActivity().runOnUiThread(() -> Appodeal.setTriggerOnLoadedOnPrecache(getAdType(adType), setOnTriggerBoth));
                return true;
            }
            case ACTION_DISABLE_LOCATION_PERMISSION_CHECK:
                cordova.getActivity().runOnUiThread(Appodeal::disableLocationPermissionCheck);
                return true;
            case ACTION_DISABLE_WRITE_EXTERNAL_STORAGE_CHECK:
                cordova.getActivity().runOnUiThread(Appodeal::disableWriteExternalStoragePermissionCheck);
                return true;
            case ACTION_MUTE_VIDEOS_IF_CALLS_MUTED: {
                final boolean value = args.getBoolean(0);
                cordova.getActivity().runOnUiThread(() -> Appodeal.muteVideosIfCallsMuted(value));
                return true;
            }
            case ACTION_START_TEST_ACTIVITY:
                cordova.getActivity().runOnUiThread(() -> Appodeal.startTestActivity(cordova.getActivity()));
                return true;
            case ACTION_SET_PLUGIN_VERSION:
                final String pluginVersion = args.getString(0);
                cordova.getActivity().runOnUiThread(() -> Appodeal.setFramework("cordova", pluginVersion));
                return true;
            case ACTION_GET_VERSION:
                cordova.getActivity().runOnUiThread(() -> callback.success(Appodeal.getVersion()));
                return true;
            case ACTION_CAN_SHOW: {
                final int adType = args.getInt(0);
                cordova.getActivity().runOnUiThread(() -> {
                    if (Appodeal.canShow(getAdType(adType))) {
                        callback.sendPluginResult(new PluginResult(PluginResult.Status.OK, true));
                    } else {
                        callback.sendPluginResult(new PluginResult(PluginResult.Status.OK, false));
                    }
                });
                return true;
            }
            case ACTION_CAN_SHOW_WITH_PLACEMENT: {
                final int adType = args.getInt(0);
                final String placement = args.getString(1);
                cordova.getActivity().runOnUiThread(() -> {
                    if (Appodeal.canShow(getAdType(adType), placement)) {
                        callback.sendPluginResult(new PluginResult(PluginResult.Status.OK, true));
                    } else {
                        callback.sendPluginResult(new PluginResult(PluginResult.Status.OK, false));
                    }
                });
                return true;
            }
            case ACTION_SET_CUSTOM_INTEGER_RULE: {
                final String name = args.getString(0);
                final int value = args.getInt(1);
                cordova.getActivity().runOnUiThread(() -> Appodeal.setExtraData(name, value));
                return true;
            }
            case ACTION_SET_CUSTOM_BOOLEAN_RULE: {
                final String name = args.getString(0);
                final boolean value = args.getBoolean(1);
                cordova.getActivity().runOnUiThread(() -> Appodeal.setExtraData(name, value));
                return true;
            }
            case ACTION_SET_CUSTOM_DOUBLE_RULE: {
                final String name = args.getString(0);
                final double value = args.getDouble(1);
                cordova.getActivity().runOnUiThread(() -> Appodeal.setExtraData(name, value));
                return true;
            }
            case ACTION_SET_CUSTOM_STRING_RULE: {
                final String name = args.getString(0);
                final String value = args.getString(1);
                cordova.getActivity().runOnUiThread(() -> Appodeal.setExtraData(name, value));
                return true;
            }
            case ACTION_GET_REWARD_PARAMETERS:
                cordova.getActivity().runOnUiThread(() -> {
                    try {
                        JSONObject vals = new JSONObject();
                        vals.put("amount", Appodeal.getRewardParameters().first);
                        vals.put("currency", Appodeal.getRewardParameters().second);
                        callback.sendPluginResult(new PluginResult(PluginResult.Status.OK, vals));
                    } catch (JSONException ignored) {
                    }
                });
                return true;
            case ACTION_GET_REWARD_PARAMETERS_FOR_PLACEMENT: {
                final String placement = args.getString(0);
                cordova.getActivity().runOnUiThread(() -> {
                    try {
                        JSONObject vals = new JSONObject();
                        vals.put("amount", Appodeal.getRewardParameters(placement).first);
                        vals.put("currency", Appodeal.getRewardParameters(placement).second);
                        callback.sendPluginResult(new PluginResult(PluginResult.Status.OK, vals));
                    } catch (JSONException ignored) {
                    }
                });
                return true;
            }
            case ACTION_SET_AGE:
                final int age = args.getInt(0);
                cordova.getActivity().runOnUiThread(() -> getUserSettings().setAge(age));
                return true;
            case ACTION_SET_GENDER:
                final String gender = args.getString(0);
                cordova.getActivity().runOnUiThread(() -> {
                    if (gender.equals("other".toLowerCase())) {
                        getUserSettings().setGender(UserSettings.Gender.OTHER);
                    } else if (gender.equals("female".toLowerCase())) {
                        getUserSettings().setGender(UserSettings.Gender.FEMALE);
                    } else if (gender.equals("male".toLowerCase())) {
                        getUserSettings().setGender(UserSettings.Gender.MALE);
                    }
                });
                return true;
            case ACTION_SET_USER_ID:
                final String userId = args.getString(0);
                cordova.getActivity().runOnUiThread(() -> getUserSettings().setUserId(userId));
                return true;
            case ACTION_SET_INTERSTITIAL_CALLBACKS:
                cordova.getActivity().runOnUiThread(() -> {
                    try {
                        interstitialCallbacks = callback;
                        Appodeal.setInterstitialCallbacks(interstitialListener);
                        JSONObject values = new JSONObject();
                        values.put("event", CALLBACK_INIT);
                        PluginResult result = new PluginResult(PluginResult.Status.OK, values);
                        result.setKeepCallback(true);
                        callback.sendPluginResult(result);
                    } catch (JSONException ignored) {
                    }
                });
                return true;
            case ACTION_SET_NON_SKIPPABLE_VIDEO_CALLBACKS:
                cordova.getActivity().runOnUiThread(() -> {
                    try {
                        nonSkippableCallbacks = callback;
                        Appodeal.setNonSkippableVideoCallbacks(nonSkippableVideoListener);
                        JSONObject values = new JSONObject();
                        values.put("event", CALLBACK_INIT);
                        PluginResult result = new PluginResult(PluginResult.Status.OK, values);
                        result.setKeepCallback(true);
                        callback.sendPluginResult(result);
                    } catch (JSONException ignored) {
                    }
                });
                return true;
            case ACTION_SET_REWARDED_CALLBACKS:
                cordova.getActivity().runOnUiThread(() -> {
                    try {
                        rewardedCallbacks = callback;
                        Appodeal.setRewardedVideoCallbacks(rewardedVideoListener);
                        JSONObject values = new JSONObject();
                        values.put("event", CALLBACK_INIT);
                        PluginResult result = new PluginResult(PluginResult.Status.OK, values);
                        result.setKeepCallback(true);
                        callback.sendPluginResult(result);
                    } catch (JSONException ignored) {
                    }
                });
                return true;
            case ACTION_SET_BANNER_CALLBACKS:
                cordova.getActivity().runOnUiThread(() -> {
                    try {
                        bannerCallbacks = callback;
                        Appodeal.setBannerCallbacks(bannerListener);
                        JSONObject values = new JSONObject();
                        values.put("event", CALLBACK_INIT);
                        PluginResult result = new PluginResult(PluginResult.Status.OK, values);
                        result.setKeepCallback(true);
                        callback.sendPluginResult(result);
                    } catch (JSONException ignored) {
                    }
                });
                return true;
        }
        return false;
    }

    private UserSettings getUserSettings() {
        if(userSettings == null) {
            userSettings = Appodeal.getUserSettings(cordova.getActivity());
        }
        return userSettings;
    }

    private int getAdType(int adType) {
        int type = 0;
        if((adType & 3) > 0) {
            type |= Appodeal.INTERSTITIAL;
        }
        if((adType & 4) > 0) {
            type |= Appodeal.BANNER;
        }
        if((adType & 8) > 0) {
            type |= Appodeal.BANNER_BOTTOM;
        }
        if((adType & 16) > 0) {
            type |= Appodeal.BANNER_TOP;
        }
        if((adType & 128) > 0) {
            type |= Appodeal.REWARDED_VIDEO;
        }
        if((adType & 256) > 0) {
            type |= Appodeal.NON_SKIPPABLE_VIDEO;
        }
        return type;
    }

    private InterstitialCallbacks interstitialListener = new InterstitialCallbacks() {

        @Override
        public void onInterstitialShown() {
            cordova.getActivity().runOnUiThread(() -> {
                try {
                    JSONObject values = new JSONObject();
                    values.put("event", CALLBACK_SHOWN);
                    PluginResult result = new PluginResult(PluginResult.Status.OK, values);
                    result.setKeepCallback(true);
                    interstitialCallbacks.sendPluginResult(result);
                } catch (JSONException ignored){}
            });
        }

        @Override
        public void onInterstitialShowFailed() {
            cordova.getActivity().runOnUiThread(() -> {
                try {
                    JSONObject values = new JSONObject();
                    values.put("event", CALLBACK_SHOW_FAILED);
                    PluginResult result = new PluginResult(PluginResult.Status.OK, values);
                    result.setKeepCallback(true);
                    interstitialCallbacks.sendPluginResult(result);
                } catch (JSONException ignored){}
            });
        }

        @Override
        public void onInterstitialLoaded(final boolean arg0) {
            cordova.getActivity().runOnUiThread(() -> {
                try {
                    JSONObject values = new JSONObject();
                    values.put("event", CALLBACK_LOADED);
                    values.put("isPrecache", arg0);
                    PluginResult result = new PluginResult(PluginResult.Status.OK, values);
                    result.setKeepCallback(true);
                    interstitialCallbacks.sendPluginResult(result);
                } catch (JSONException ignored){}
            });
        }

        @Override
        public void onInterstitialFailedToLoad() {
            cordova.getActivity().runOnUiThread(() -> {
                try {
                    JSONObject values = new JSONObject();
                    values.put("event", CALLBACK_FAILED);
                    PluginResult result = new PluginResult(PluginResult.Status.OK, values);
                    result.setKeepCallback(true);
                    interstitialCallbacks.sendPluginResult(result);
                } catch (JSONException ignored){}
            });
        }

        @Override
        public void onInterstitialClosed() {
            cordova.getActivity().runOnUiThread(() -> {
                try {
                    JSONObject values = new JSONObject();
                    values.put("event", CALLBACK_CLOSED);
                    PluginResult result = new PluginResult(PluginResult.Status.OK, values);
                    result.setKeepCallback(true);
                    interstitialCallbacks.sendPluginResult(result);
                } catch (JSONException ignored){}
            });
        }

        @Override
        public void onInterstitialExpired() {
            cordova.getActivity().runOnUiThread(() -> {
                try {
                    JSONObject values = new JSONObject();
                    values.put("event", CALLBACK_EXPIRED);
                    PluginResult result = new PluginResult(PluginResult.Status.OK, values);
                    result.setKeepCallback(true);
                    interstitialCallbacks.sendPluginResult(result);
                } catch (JSONException ignored){}
            });
        }

        @Override
        public void onInterstitialClicked() {
            cordova.getActivity().runOnUiThread(() -> {
                try {
                    JSONObject values = new JSONObject();
                    values.put("event", CALLBACK_CLICKED);
                    PluginResult result = new PluginResult(PluginResult.Status.OK, values);
                    result.setKeepCallback(true);
                    interstitialCallbacks.sendPluginResult(result);
                } catch (JSONException ignored){}
            });
        }
    };

    private NonSkippableVideoCallbacks nonSkippableVideoListener = new NonSkippableVideoCallbacks() {

        @Override
        public void onNonSkippableVideoClosed(final boolean finished) {
            cordova.getActivity().runOnUiThread(() -> {
                try {
                    JSONObject values = new JSONObject();
                    values.put("event", CALLBACK_CLOSED);
                    values.put("finished", finished);
                    PluginResult result = new PluginResult(PluginResult.Status.OK, values);
                    result.setKeepCallback(true);
                    nonSkippableCallbacks.sendPluginResult(result);
                } catch (JSONException ignored){}
            });
        }

        @Override
        public void onNonSkippableVideoExpired() {
            cordova.getActivity().runOnUiThread(() -> {
                try {
                    JSONObject values = new JSONObject();
                    values.put("event", CALLBACK_EXPIRED);
                    PluginResult result = new PluginResult(PluginResult.Status.OK, values);
                    result.setKeepCallback(true);
                    nonSkippableCallbacks.sendPluginResult(result);
                } catch (JSONException ignored){}
            });
        }

        @Override
        public void onNonSkippableVideoLoaded(boolean arg0) {
            cordova.getActivity().runOnUiThread(() -> {
                try {
                    JSONObject values = new JSONObject();
                    values.put("event", CALLBACK_LOADED);
                    values.put("isPrecache", arg0);
                    PluginResult result = new PluginResult(PluginResult.Status.OK, values);
                    result.setKeepCallback(true);
                    nonSkippableCallbacks.sendPluginResult(result);
                } catch (JSONException ignored){}
            });
        }

        @Override
        public void onNonSkippableVideoFailedToLoad() {
            cordova.getActivity().runOnUiThread(() -> {
                try {
                    JSONObject values = new JSONObject();
                    values.put("event", CALLBACK_FAILED);
                    PluginResult result = new PluginResult(PluginResult.Status.OK, values);
                    result.setKeepCallback(true);
                    nonSkippableCallbacks.sendPluginResult(result);
                } catch (JSONException ignored){}
            });
        }

        @Override
        public void onNonSkippableVideoFinished() {
            cordova.getActivity().runOnUiThread(() -> {
                try {
                    JSONObject values = new JSONObject();
                    values.put("event", CALLBACK_FINISHED);
                    PluginResult result = new PluginResult(PluginResult.Status.OK, values);
                    result.setKeepCallback(true);
                    nonSkippableCallbacks.sendPluginResult(result);
                } catch (JSONException ignored){}
            });
        }

        @Override
        public void onNonSkippableVideoShown() {
            cordova.getActivity().runOnUiThread(() -> {
                try {
                    JSONObject values = new JSONObject();
                    values.put("event", CALLBACK_SHOWN);
                    PluginResult result = new PluginResult(PluginResult.Status.OK, values);
                    result.setKeepCallback(true);
                    nonSkippableCallbacks.sendPluginResult(result);
                } catch (JSONException ignored){}
            });
        }

        @Override
        public void onNonSkippableVideoShowFailed() {
            cordova.getActivity().runOnUiThread(() -> {
                try {
                    JSONObject values = new JSONObject();
                    values.put("event", CALLBACK_SHOW_FAILED);
                    PluginResult result = new PluginResult(PluginResult.Status.OK, values);
                    result.setKeepCallback(true);
                    nonSkippableCallbacks.sendPluginResult(result);
                } catch (JSONException ignored){}
            });
        }

    };

    private RewardedVideoCallbacks rewardedVideoListener = new RewardedVideoCallbacks() {

        @Override
        public void onRewardedVideoClosed(final boolean finished) {
            cordova.getActivity().runOnUiThread(() -> {
                try {
                    JSONObject values = new JSONObject();
                    values.put("event", CALLBACK_CLOSED);
                    values.put("finished", finished);
                    PluginResult result = new PluginResult(PluginResult.Status.OK, values);
                    result.setKeepCallback(true);
                    rewardedCallbacks.sendPluginResult(result);
                } catch (JSONException ignored){}
            });
        }

        @Override
        public void onRewardedVideoExpired() {
            cordova.getActivity().runOnUiThread(() -> {
                try {
                    JSONObject values = new JSONObject();
                    values.put("event", CALLBACK_EXPIRED);
                    PluginResult result = new PluginResult(PluginResult.Status.OK, values);
                    result.setKeepCallback(true);
                    rewardedCallbacks.sendPluginResult(result);
                } catch (JSONException ignored){}
            });
        }

        @Override
        public void onRewardedVideoClicked() {
            cordova.getActivity().runOnUiThread(() -> {
                try {
                    JSONObject values = new JSONObject();
                    values.put("event", CALLBACK_CLICKED);
                    PluginResult result = new PluginResult(PluginResult.Status.OK, values);
                    result.setKeepCallback(true);
                    rewardedCallbacks.sendPluginResult(result);
                } catch (JSONException ignored){}
            });
        }

        @Override
        public void onRewardedVideoLoaded(boolean b) {
            cordova.getActivity().runOnUiThread(() -> {
                try {
                    JSONObject values = new JSONObject();
                    values.put("event", CALLBACK_LOADED);
                    values.put("isPrecache", b);
                    PluginResult result = new PluginResult(PluginResult.Status.OK, values);
                    result.setKeepCallback(true);
                    rewardedCallbacks.sendPluginResult(result);
                } catch (JSONException ignored){}
            });
        }

        @Override
        public void onRewardedVideoFailedToLoad() {
            cordova.getActivity().runOnUiThread(() -> {
                try {
                    JSONObject values = new JSONObject();
                    values.put("event", CALLBACK_FAILED);
                    PluginResult result = new PluginResult(PluginResult.Status.OK, values);
                    result.setKeepCallback(true);
                    rewardedCallbacks.sendPluginResult(result);
                } catch (JSONException ignored){}
            });
        }

        @Override
        public void onRewardedVideoShown() {
            cordova.getActivity().runOnUiThread(() -> {
                try {
                    JSONObject values = new JSONObject();
                    values.put("event", CALLBACK_SHOWN);
                    PluginResult result = new PluginResult(PluginResult.Status.OK, values);
                    result.setKeepCallback(true);
                    rewardedCallbacks.sendPluginResult(result);
                } catch (JSONException ignored){}
            });
        }

        @Override
        public void onRewardedVideoShowFailed() {
            cordova.getActivity().runOnUiThread(() -> {
                try {
                    JSONObject values = new JSONObject();
                    values.put("event", CALLBACK_SHOW_FAILED);
                    PluginResult result = new PluginResult(PluginResult.Status.OK, values);
                    result.setKeepCallback(true);
                    rewardedCallbacks.sendPluginResult(result);
                } catch (JSONException ignored){}
            });
        }

        @Override
        public void onRewardedVideoFinished(double v, String s) {
            cordova.getActivity().runOnUiThread(() -> {
                try {
                    JSONObject values = new JSONObject();
                    values.put("event", CALLBACK_FINISHED);
                    values.put("amount", v);
                    values.put("name", s);
                    PluginResult result = new PluginResult(PluginResult.Status.OK, values);
                    result.setKeepCallback(true);
                    rewardedCallbacks.sendPluginResult(result);
                } catch (JSONException ignored){}
            });
        }

    };

    private BannerCallbacks bannerListener = new BannerCallbacks() {

        @Override
        public void onBannerClicked() {
            cordova.getActivity().runOnUiThread(() -> {
                try {
                    JSONObject values = new JSONObject();
                    values.put("event", CALLBACK_CLICKED);
                    PluginResult result = new PluginResult(PluginResult.Status.OK, values);
                    result.setKeepCallback(true);
                    bannerCallbacks.sendPluginResult(result);
                } catch (JSONException ignored){}
            });
        }

        @Override
        public void onBannerExpired() {
            cordova.getActivity().runOnUiThread(() -> {
                try {
                    JSONObject values = new JSONObject();
                    values.put("event", CALLBACK_EXPIRED);
                    PluginResult result = new PluginResult(PluginResult.Status.OK, values);
                    result.setKeepCallback(true);
                    bannerCallbacks.sendPluginResult(result);
                } catch (JSONException ignored){}
            });
        }

        @Override
        public void onBannerFailedToLoad() {
            cordova.getActivity().runOnUiThread(() -> {
                try {
                    JSONObject values = new JSONObject();
                    values.put("event", CALLBACK_FAILED);
                    PluginResult result = new PluginResult(PluginResult.Status.OK, values);
                    result.setKeepCallback(true);
                    bannerCallbacks.sendPluginResult(result);
                } catch (JSONException ignored){}
            });
        }

        @Override
        public void onBannerLoaded(final int height, final boolean isPrecache) {
            cordova.getActivity().runOnUiThread(() -> {
                try {
                    JSONObject values = new JSONObject();
                    values.put("event", CALLBACK_LOADED);
                    values.put("height", height);
                    values.put("isPrecache", isPrecache);
                    PluginResult result = new PluginResult(PluginResult.Status.OK, values);
                    result.setKeepCallback(true);
                    bannerCallbacks.sendPluginResult(result);
                } catch (JSONException ignored){}
            });
        }

        @Override
        public void onBannerShown() {
            cordova.getActivity().runOnUiThread(() -> {
                try {
                    JSONObject values = new JSONObject();
                    values.put("event", CALLBACK_SHOWN);
                    PluginResult result = new PluginResult(PluginResult.Status.OK, values);
                    result.setKeepCallback(true);
                    bannerCallbacks.sendPluginResult(result);
                } catch (JSONException ignored){}
            });
        }

        @Override
        public void onBannerShowFailed() {
            cordova.getActivity().runOnUiThread(() -> {
                try {
                    JSONObject values = new JSONObject();
                    values.put("event", CALLBACK_SHOW_FAILED);
                    PluginResult result = new PluginResult(PluginResult.Status.OK, values);
                    result.setKeepCallback(true);
                    bannerCallbacks.sendPluginResult(result);
                } catch (JSONException ignored){}
            });
        }

    };

    private ViewGroup getViewGroup(int child){
        ViewGroup vg = this.cordova.getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
        if(child != -1) vg = (ViewGroup)vg.getChildAt(child);
        return vg;
    }

    private boolean showBanner(int adType, String placement){
        if (bannerView != null && bannerView.getParent() != null) {
            ((ViewGroup)bannerView.getParent()).removeView(bannerView);
        }
        if (bannerView == null) bannerView = Appodeal.getBannerView(cordova.getActivity());

        if (bannerOverlap){
            ViewGroup rootView = getViewGroup(-1);
            if(rootView == null) return false;
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            if(adType == Appodeal.BANNER_TOP) params.gravity = Gravity.TOP  | Gravity.CENTER_HORIZONTAL;
            else params.gravity = Gravity.BOTTOM  | Gravity.CENTER_HORIZONTAL;
            rootView.addView(bannerView, params);
            rootView.requestLayout();
        } else {
            ViewGroup rootView = getViewGroup(0);
            if(rootView == null) return false;
            if (parentView == null) {
                parentView = new LinearLayout(cordova.getActivity());
            }
            if (rootView != parentView) {
                ((ViewGroup)rootView.getParent()).removeView(rootView);
                ((LinearLayout) parentView).setOrientation(LinearLayout.VERTICAL);
                parentView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0.0F));
                rootView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1.0F));
                parentView.addView(rootView);
                cordova.getActivity().setContentView(parentView);
            }

            if(adType == Appodeal.BANNER_TOP)
                parentView.addView(bannerView, 0);
            else
                parentView.addView(bannerView);

            parentView.bringToFront();
            parentView.requestLayout();
        }
        boolean res;
        if(placement == null) res = Appodeal.show(cordova.getActivity(), Appodeal.BANNER_VIEW);
        else res = Appodeal.show(cordova.getActivity(), Appodeal.BANNER_VIEW, placement);

        return res;
    }
}