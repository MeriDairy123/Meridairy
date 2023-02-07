package b2infosoft.milkapp.com.appglobal;

import android.app.Dialog;
import android.content.Context;
import android.view.Menu;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Model.CowBuffaloSNFPojo;
import b2infosoft.milkapp.com.Model.SnfFatListPojo;

/**
 * Created by B2infosoft on 7/26/2017.
 */

public class Constant {
    public static final String FCMCHANNEL_ID = "default";
    public static final String FCMCHANNEL_NAME = "Meri Dairy";
    public static final String FCMCHANNEL_DESCRIPTION = "Meri Dairy Description";

    /* Live url */
      static String ApiUrl = "https://meridairy.in/software/api/";
      static String ApiChartUrl = "https://meridairy.in/software/";
      static String ApkUrl = "https://meridairy.in/software/";

    //b2y server testing
//    static String ApiUrl = "http://b2y.in/software/api/";
//    static String ApkUrl = "http://b2y.in/software/";
//    static String ApiChartUrl = "http://b2y.in/software/";

    // Location service related values
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final int PICK_CONTACT = 108;
    public static final String MAPDirectionAPI = "https://maps.googleapis.com/maps/api/directions/";
    public static final String LOCATION_RECEIVER = "LOCATION_RECEIVER";
    public static final String RESULT_DATA_KEY = "RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = "LOCATION_DATA_EXTRA";
    public static final String ADDRESS_DATA_EXTRA = "ADDRESS_DATA_EXTRA";

    public static final int LOCATION_RESULT_COUNT = 5;
    public static final float DEFAULT_ZOOM = 16;
    public static final int MENU_ENGLISH_ITEM = Menu.FIRST;
    public static final int MENU_SPANISH_ITEM = Menu.FIRST + 1;
    public static final int MENU_HINDI_ITEM = Menu.FIRST + 2;
    public static final int MENU_GUJRATI_ITEM = Menu.FIRST + 3;
    public static final int MENU_MARATHI_ITEM = Menu.FIRST + 4;
    public static final int MENU_PUNJABI_ITEM = Menu.FIRST + 5;
    public static final int MENU_TELGU_ITEM = Menu.FIRST + 6;
    public static final int MENU_TAMIL_ITEM = Menu.FIRST + 7;
    public static final int MENU_KANNAD_ITEM = Menu.FIRST + 8;
    public static String TAG = "WebServiceTask";
    public static String SNFID = "";
    public static String StatusClicked = "false";
    public static String LoadToLang = "";
    public static String ForAdvt = "Yes";
    public static String MeridairySuppportMob = "9772196777";

    public static final int RC_APP_UPDATE = 11;
    public static String urlYoutubeVideo = "https://www.youtube.com/embed/iAGldlX5jks?autoplay=true";

    public static String MERCHANT_KEY = "";
    public static String MID = "";
    public static String INDUSTRY_TYPE_ID = "";
    public static String CHANNEL_ID = "";
    public static String WEBSITE = "";
    public static String CALLBACK_URL = "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=";
    public static String user_group_id = "";
    public static String BaseImageUrl = "";
    public static String AppGooglePlayStoreUrl = "https://play.google.com/store/apps/details?id=b2infosoft.milkapp.com&hl=en_IN";
    public static String AppUpdateServerUrl = ApkUrl+"meridairy.apk";
    public static Dialog dialog = null;
    public static Context ChatContext = null;
    public static String FromWhere = "", FromWhere2 = "", SelectedDate = "", strSession = "", Weight = "0.0", ViewUserEntryStartDate = "", ViewUserEntryEndDate = "", UserID = "", LangLoaded = "", tempMobileNumber = "", DairyNameID = "", DairyName = "", Month = "", Year = "", OnChat = "", DairySize = "", SNFKey = "", ctegory_ID = "", SNFKey2 = "", FirstTime = "", dairyID = "", SessionUserGroupID = "", BtnType = "", BuyerFirstTime = "", AnimalCatImgUrl = "", AnimalName = "", str_location_address = "", str_location_city = "", str_location_state = "", str_location_postCode = "", str_location_Latitude = "", str_location_Longitude = "";

    public static ArrayList<SnfFatListPojo> snfFatList = new ArrayList<>();

    public static ArrayList<CowBuffaloSNFPojo> CowBuffelloSNF_List = new ArrayList<>();


    // Sale and Buy Milk Bonous Price
    public static float BuyMilkBonusPrice = 0, SaleMilkBonusPrice = 0;
    public static int MENU_ENGLISH = 0, MENU_HINDI = 1, MENU_GUJRATI = 2, MENU_MARATHI = 3, MENU_PUNJABI = 4, MENU_TELGU = 5, MENU_TAMIL = 6, MENU_KANNAD = 7;


    public static String downloadSampleChartFileAPI = ApiChartUrl + "dairy/download-sample-file";


    //b2y server
   // static String ApiUrl = "http://b2y.in/api/";

    // Dairy Customers Invoice
    public static String getDairyCustomersInvoiceListAPI = ApiUrl + "admin/get-dairy-owner-customers-invoices";
    public static String generateBuyerInvoiceAPI = ApiUrl + "admin/generate-buyer-invoice";
    public static String generateSellerInvoiceAPI = ApiUrl + "admin/generate-seller-invoice";
    // Product
    public static String addProductAPI = ApiUrl + "admin/add-product?";
    public static String getProductListAPI = ApiUrl + "admin/get-product?";
    public static String editProductAPI = ApiUrl + "admin/edit-product?";
    public static String getAllDairyNameAndTransactionAPI = ApiUrl + "admin/get-all-dairy-name-and-transactions?";
    public static String addUserProductAPI = ApiUrl + "admin/user-product-add";

    // Product Sale and Purchase
    public static String getUserProductListAPI = ApiUrl + "admin/user-product-list";
    // Purchase Invoice
    public static String addPurchaseInvoiceAPI = ApiUrl + "admin/add-purchase-invoice";
    public static String getPurchaseInvoiceListAPI = ApiUrl + "admin/purchase-invoice-list";
    // Sale Invoice
    public static String addSaleInvoiceAPI = ApiUrl + "admin/add-sale-invoice";
    public static String getSaleInvoiceListAPI = ApiUrl + "admin/sale-invoice-list";


    // Sale Reture Invoice
    public static String addSaleReturnInvoiceAPI = ApiUrl + "admin/add-salereturn-invoice";
    // payment Voucher
    public static String getUserTransactionBalanceAPI = ApiUrl + "admin/user-transaction-balance-list";
    public static String getPayVoucherListAPI = ApiUrl + "admin/payment-voucher-list";
    public static String addPayVoucherAPI = ApiUrl + "admin/add-payment-voucher";
    public static String addRecieptVoucherAPI = ApiUrl + "admin/add-receipt-voucher";
    public static String getRecieptVoucherListAPI = ApiUrl + "admin/receipt-voucher-list";
    public static String addProductGroupAPI = ApiUrl + "admin/product-group-add";
    public static String updateProductGroupAPI = ApiUrl + "admin/product-group-update";
    public static String getProductGroupListAPI = ApiUrl + "admin/product-group-list";
    //Product Item Group
    public static String addProductItemGroupAPI = ApiUrl + "admin/product-item-group-add";
    public static String updateProductBrandItemGroupAPI = ApiUrl + "admin/product-item-group-update";
    public static String getProdItemGroupListAPI = ApiUrl + "admin/product-item-group-list";
    //Brand
    public static String addProductBrandAPI = ApiUrl + "admin/product-brand-add";
    public static String updateProductBrandAPI = ApiUrl + "admin/product-brand-update";
    public static String getProductBrandListAPI = ApiUrl + "admin/product-brand-list";
    public static String getUnitListAPI = ApiUrl + "admin/get-unit-list";
    //Transactions
    public static String getUserTransactionAPI = ApiUrl + "admin/get-user-transactions-full-details";

    //Paytm
    public static String getPaymentCallBackAPI = ApiUrl + "paytm-callback-api?";
    public static String getChecksumFromArray = ApiUrl + "dairy/paytm-payment-checksum";
    public static String orderSaveAPI = ApiUrl + "admin/order-details-save";
    public static String getOrderListAPI = ApiUrl + "admin/get-user-order-details/";
    public static String getDairyCustomersOrderListAPI = ApiUrl + "admin/dairy-customers-order/";
    public static String dairyUpdateCustomersOrderStatusAPI = ApiUrl + "admin/dairy-update-order-status";
    public static String getShippingDetailsAPI = ApiUrl + "admin/get-shipping-details";
    public static String getOrderDeliveryAPI = ApiUrl + "admin/get-order-delivery-details";
    public static String getOrderCancelAPI = ApiUrl + "admin/get-order-cancels-details";
    public static String deliverBoyListAPI = ApiUrl + "admin/delivery-boy-listing";
    public static String addDeliverBoyAPI = ApiUrl + "admin/add-deliveryboy";
    public static String updateDeliverBoyAPI = ApiUrl + "admin/update-deliveryboy";
    public static String deleteDeliverBoyAPI = ApiUrl + "admin/delete-deliveryboy";
    public static String assignDeliverBoyAPI = ApiUrl + "admin/assign-delivery-boy-to-users";
    public static String deliverUserListAPI = ApiUrl + "admin/get-all-user-list";
    public static String deliverSaleMilkEntryAPI = ApiUrl + "admin/deliveryboy-sale-milk-entry-customer";
    public static final String customerLoginAPI = ApiUrl + "admin/login-customer?";

    public static final String getPlanRequestAPI = ApiUrl + "admin/get-plan-request";
    public static final String acceptPlanRequest = ApiUrl + "admin/plan-request-accept";
    // public static final String GetMonthlyData = ApiUrl + "admin/get-total-milk-price?";

    public static String getStateAPI = ApiUrl + "admin/get-state";
    public static String loginAPI = ApiUrl + "admin/login?";
    public static String requestPasswordAPI = ApiUrl + "admin/request-password?";
    public static String registrationAPI = ApiUrl + "admin/register-dairy?";
    public static String updateDairyProfile = ApiUrl + "admin/update-dairy-profile";
    public static String loginByQRCodeAPI = ApiUrl + "admin/login-by-qrcode";
    public static String webQRCodeLoginAPI = ApiUrl + "admin/update-qrcode-user_id";
    public static String logoutFromQRCodeAPI = ApiUrl + "admin/logout-from-another-devices";
    public static String getOTPAPI = ApiUrl + "admin/get-forgot-password";
    public static String updatePasswordAPI = ApiUrl + "admin/update-password";
    public static String resetPasswordInAppAPI = ApiUrl + "admin/reset-password-by-app?";
    public static String getCityAPI = ApiUrl + "admin/get-city?state_id=@state_id";
    public static String checkActivateKeyAPI = ApiUrl + "admin/check-activate-user?";
    //   Buy Milk Entry
    public static String getBuyMilkEntry3MonthAPI = ApiUrl + "admin/get-milk-entry-last-three-months";
    public static String getBonusAPI = ApiUrl + "admin/get-bonus?";
    public static String updateBonusAPI = ApiUrl + "admin/update-bonus?";
    public static String getMilkPriceAPI = ApiUrl + "admin/get-milk-price?dairy_id=@dairy_id";
    public static String updateMilkPriceAPI = ApiUrl + "admin/update-milk-price?dairy_id=@dairy_id&milk_price=@milk_price";
    public static String getCustomerListAPI = ApiUrl + "admin/get-customer?";
    public static String getBuyMilkEntryAPI = ApiUrl + "admin/get-buy-milk-daily-entry";

    public static String getViewEntryBothShiftsAPI = ApiUrl + "admin/get-view-entry-both-shifts";
    public static String addBuyMilkEntryAPI = ApiUrl + "admin/milk-entry-add?";
    public static String updateBuyMilkEntryAPI = ApiUrl + "admin/milk-entry-update?";
    public static String deleteBuyMilkEntryAPI = ApiUrl + "admin/milk-entry-delete?";
    public static String uploadOfflineBuyMilkEntryToServerAPI = ApiUrl + "admin/bulk-milk-entry-add?";
    public static String getBuyMilkEntryByTwoDateAPI = ApiUrl + "admin/get-customer-entry-with-two?";
    //   Sale Milk Entry
    public static String getSaleMilkEntry3MonthAPI = ApiUrl + "admin/get-sale-milk-entry-last-three-months";
    public static String getSaleFatMilkPriceAPI = ApiUrl + "admin/get-milk-price-sale?dairy_id=@dairy_id";
    public static String updateSaleFatMilkPriceAPI = ApiUrl + "admin/update-milk-price-sale?dairy_id=@dairy_id&milk_price=@milk_price";
    public static String addSaleMilkEntryAPI = ApiUrl + "admin/milk-entry-sale?";
    public static String updateSaleMilkEntryAPI = ApiUrl + "admin/milk-entry-update-sale?";
    public static String deleteSaleMilkEntryAPI = ApiUrl + "admin/milk-entry-delete-sale?";
    public static String uploadOfflineSaleEntryToServerAPI = ApiUrl + "admin/bulk-milk-entry-sale-new?";
    public static String getSaleMilkEntryAPI = ApiUrl + "admin/get-sale-milk-daily-entry";
    public static String getSellingMilkPriceAPI = ApiUrl + "admin/get-shell-milk-price?";
    public static String updateSellingMilkPriceAPI = ApiUrl + "admin/add-shell-milk-price?";
    public static String addSellingMilkAPI = ApiUrl + "admin/save-shell-milk-data?";
    public static String getSaleMilkEntryListCustomerAPI = ApiUrl + "admin/get-shell-milk-data?";
    public static String getSaleMilkEntryListAPI = ApiUrl + "admin/get-shell-milk-data-by-shift?";
    public static String getSaleMilkEntryListEditUpdateAPI = ApiUrl + "admin/update-shell-milk-data?";
    public static String getSaleMilkEntryListDeleteAPI = ApiUrl + "admin/delete-shell-milk-data?";
    public static String deleteSaleMilkAPI = ApiUrl + "admin/delete-shell-milk-data?";
    public static String getDailySaleMilkCustomerListAPI = ApiUrl + "admin/get-active-multy-milkentry-user-lists?";
    public static String DailySaveMilkSchedule = ApiUrl + "admin/save-shell-milk-schedules?";
    public static String UpdateDailySaveMilkEntry = ApiUrl + "admin/update-shell-milk-data?";
    // Send Message or Notification
    public static String notificationMessage = ApiUrl + "admin/notification-message";
    // Vehicle Plant Entry
    public static String getVehicleCustomer = ApiUrl + "admin/vehicle-customers-list";
    // Delivery Boy
    public static String NewBuyerRegister = ApiUrl + "admin/register-dairy-customer";
    public static String CheckBuyerMobile = ApiUrl + "admin/check-customer-mobile?";
    public static String SellProduct = ApiUrl + "admin/store-product-sell";
    public static String DeleteProduct = ApiUrl + "admin/delete-product?";
    public static String getTransaction = ApiUrl + "admin/get-transactions?";

    public static String SaveSignature = ApiUrl + "admin/save-signatures?";
    public static String GetSignature = ApiUrl + "admin/get-signatures-image?";
    public static String GetSaleProductList = ApiUrl + "admin/get-total-shell-product?";
    // Milk Plan
    public static String addMilkPlan = ApiUrl + "admin/add-dairy-milk-product-plan";
    public static String getMilkPlan = ApiUrl + "admin/get-dairy-milk-product-plan";
    public static String updateMilkPlan = ApiUrl + "admin/update-dairy-milk-product-plan";
    public static String deleteMilkPlan = ApiUrl + "admin/delete-dairy-milk-product-plan";
    // Customer In Dairy
    public static String AddCustomer = ApiUrl + "admin/customer-register?";
    public static String getAllCustomer = ApiUrl + "admin/get-all-customer-list";
    public static String getEmployee = ApiUrl + "admin/get-employee";
    public static String updateCustomerAPI = ApiUrl + "admin/update-customer-register?";
    public static String deleteCustomerAPI = ApiUrl + "admin/delete-customer-register?";
    public static String getMilkHistoryAPI = ApiUrl + "admin/ten-days-milk-record?";
    public static String eraseMilkHistoryAPI = ApiUrl + "admin/ten-days-milk-record-delete?";
    public static String receiveAmountAPI = ApiUrl + "admin/receive-payment?";
    // public static String getBuyerMilkListAPI = ApiUrl + "admin/get-multy-milkentry-user-lists?";
    public static String getBuyerMilkListAPI = ApiUrl + "admin/get-buyer-customer-list";
    public static String updateBuyerMilkEntryAPI = ApiUrl + "admin/single-update-multy-milkentry-user-lists?";
    public static String updateBuyerMilkEntryStatusAPI = ApiUrl + "admin/change-status-multy-milkentry-user-lists?";
    // FAT SNF
    public static String getDairyAllChartDataAPI = ApiUrl + "admin/get-dairy-all-chart-data";
    public static String getSnfFatChartAPI = ApiUrl + "admin/get-snffat-list-data?";
    public static String getSnfFatListNewAPI = ApiUrl + "admin/get-snffat-list-data-new?";
    public static String uploadFatSnfFileAPI = ApiUrl + "admin/upload-fat-snf-chart";
    public static String getSnfFatListAPI = ApiUrl + "admin/set-value-all-list?";
    public static String updateFatRateAPI = ApiUrl + "admin/update-set-value?";
    public static String updateFatRateNewAPI = ApiUrl + "admin/update-set-value-new?";
    public static String createFatRateChartAPI = ApiUrl + "admin/make-fat-and-snf-chart";

    public static String updateCustomerMilkRateAPI = ApiUrl + "admin/milk-entry-setting-add-edit?";
    public static String updateSnfHistoryAPI = ApiUrl + "admin/snf-fat-update-histories?";
    public static String updateSnfHistoryDeleteAPI = ApiUrl + "admin/snf-fat-update-histories-delete?";
    public static String buyerMonthlyRecordListAPI = ApiUrl + "admin/sell-milk-customer-data?";
    public static String adsBannerAPI = ApiUrl + "admin/ads-banner?";
    public static String getDairyAdsAPI = ApiUrl + "admin/get-dairy-advertisement?";
    public static String getMonthlyMilkSellEntryAPI = ApiUrl + "admin/get-monthly-milk-sell-entry?";
    public static String getTenDayMilkSellEntryAPI = ApiUrl + "admin/get-seller-ten-day-history-new?";
    public static String sendMultyMessageAPI = ApiUrl + "admin/send-multy-messsage-for-pdf?";
    public static String getMultiSellerTenDaysDataAPI = ApiUrl + "admin/get-multi-seller-ten-days-data?";
    public static String getMultiUserTenDaysEntryWithTransDataAPI = ApiUrl + "admin/get-multi-user-ten-days-milkentry-with-transactions";
    public static String getBuyerCustomerDataListAPI = ApiUrl + "admin/get-buyer-customer-one-month-data-list?";
    public static String getBuyerCustomerDataListForPDFAPI = ApiUrl + "admin/get-buyer-one-month-history-pdf?";
    public static String updateCustomerMobileNumberAPI = ApiUrl + "admin/update-mobile-number?";
    public static String productDetailsListAPI = ApiUrl + "admin/store-product-sell-all-customer-list?";
    //  Setting
    public static String buyMilkRateSetting = ApiUrl + "admin/milk-entry-dairy-owner-settings-save";
    public static String saleMilkRateSetting = ApiUrl + "admin/sale-milk-entry-dairy-owner-settings-save";
    public static String smsSettingAPI = ApiUrl + "admin/user-sms-setting-save?";
    public static String getSmsSettingAPI = ApiUrl + "admin/user-sms-setting-save/";
    public static String ApiSentSms = ApiUrl + "admin/sentSms?";
    public static String getSmsBalanceAPI = ApiUrl + "admin/smsBalance/";
    public static String getPlanAPI = ApiUrl + "get-plan";
    public static String getCheckExpirePlanAPI = ApiUrl + "check-expire-days";
    public static String getBhugationSetingAPI = ApiUrl + "admin/get-bhugtan-setting";
    public static String saveBhugationSettingAPI = ApiUrl + "admin/save-bhugtan-setting";


    //CLR
    public static String getUserClrSetting = ApiUrl + "admin/get-user-clr-setting";
    public static String updateClrSettingAPI = ApiUrl + "admin/update-clr-setting";
    public static String getCategoryChartAPI = ApiUrl + "admin/categorychart";
    public static String addCategoryChartAPI = ApiUrl + "admin/categorychart-add";
    public static String updateCategoryChartAPI = ApiUrl + "admin/categorychart-update";
    public static String deleteCategoryChartAPI = ApiUrl + "admin/categorychart-delete";
    // Banner ----Shopping Section
    public static String getBannerAPI = ApiUrl + "get-banner";
    public static String getAllProductAPI = ApiUrl + "admin/get-all-product";
    public static String getBanerAPI = ApiUrl + "admin/get-banner";
    public static String getProductDetailsAPI = ApiUrl + "admin/get-product-details/";
    public static String getCitiesAPI = ApiUrl + "admin/get-user-cities?";
    public static String advertisementAPI = ApiUrl + "admin/add-dairy-advertisement";
    public static String getdairyAdvertisementAPI = ApiUrl + "admin/get-dairy-advertisement-all?";
    //  Animal Classified
    public static String getAnimalCategoryAPI = ApiUrl + "admin/get-animal-main-category";
    public static String getAnimalSubCategoryAPI = ApiUrl + "admin/get-animal-sub-category?";
    public static String registerAnimalAPI = ApiUrl + "admin/add-animal";
    public static String getAnimalsAPI = ApiUrl + "admin/get-animals?";
    public static String getDeleteAnimalsAPI = ApiUrl + "admin/delete-animal?";

    // Customer APP Buyer -seller
    public static String customerRegisterAPI = ApiUrl + "admin/register-user";
    public static String customerUpdateAPI = ApiUrl + "admin/update-user-details";
    public static final String getAllDairyNameAPI = ApiUrl + "admin/get-all-dairy-name-new?";
    public static String customerMonthDataAPI = ApiUrl + "admin/get-user-full-month-data";
    public static String customerAddVacationAPI = ApiUrl + "admin/add-vacation";
    public static String getSalMilkPlanAPI = ApiUrl + "admin/get-product-plan";
    public static String insertSaleMilkPlanAPI = ApiUrl + "admin/user-plan-insert";
    public static String getMilkPlanAPI = ApiUrl + "admin/get-user-plan";
    public static String updateMilkPlanAPI = ApiUrl + "admin/update-user-plan";
    public static String getDairyPlanAPI = ApiUrl + "admin/get-dairy-owner-productplan";
    public static String UpdateFromDairyPlanAPI = ApiUrl + "admin/dairy-plan-insert";
    public static String getDairyProductAPI = ApiUrl + "admin/get-dairy-owner-products";
    public static String getUserInvoicAPI = ApiUrl + "admin/get-user-invoice";
    public static final String getCustomerTransactionList = ApiUrl + "admin/get-user-transactions-new";
    //Seller Customer
    public static final String getSellerCustomerMonthlyData = ApiUrl + "admin/get-seller-month-wise-data-new";
    public static final String getMonthMilkEntry = ApiUrl + "admin/get-monthly-milk-entry-news";
    public static String getProductList2 = ApiUrl + "admin/get-customer-product?";
    public static String getCustomerProductDetailsListAPI = ApiUrl + "admin/get-customer-product-details";

    //Whatsapp sms
    public static String savemessagetolocal = ApiUrl+"admin/get-dairy-msg?dairy_id=";
    public static String setsmssettingdata = ApiUrl+"admin/admin-sms-setting-save";
    public static String getdairyMsg = ApiUrl+"admin/get-dairy-msg?dairy_id=";
    public static String getTotalSaleMilkDataFromServer = ApiUrl+"admin/admin-sms-setting-save";
    public static String getSmsSettingData = ApiUrl+"admin/admin-sms-setting-get";
    public static String setSmsSettingData = ApiUrl+"admin/admin-sms-setting-save";
    public static String getSmsSettingDataWhatsappsale = ApiUrl+"admin/admin-sms-setting-get";
    public static String addDairyMsg = ApiUrl+"admin/add-dairy-msg";

    //new
    public static String getcount30days = ApiUrl+"admin/count_30_days_data";
    public static String get30daysdata = ApiUrl+"admin/get_30_days_data";

    public static String get20daysdata = ApiUrl+"admin/get_20_Days_data";


    public static String smstendayssettingsave = ApiUrl+"admin/sms-tendays-setting-save";
    public static String smstendayssettingget = ApiUrl+"admin/sms-tendays-setting-get";




}
