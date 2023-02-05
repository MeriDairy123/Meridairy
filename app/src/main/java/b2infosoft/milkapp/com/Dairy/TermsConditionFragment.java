package b2infosoft.milkapp.com.Dairy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import b2infosoft.milkapp.com.Interface.FragmentBackPressListener;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;

import static b2infosoft.milkapp.com.Dairy.MainActivity.drawer;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentWithBackStack;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;

public class TermsConditionFragment extends Fragment implements View.OnClickListener, FragmentBackPressListener {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    TextView toolbar_title, tvDetails, tvAboutUs, tvPrivacy, tvTerms, tvRefundCancel, tvContactUs;
    Context mContext;
    Toolbar toolbar;

    SessionManager sessionManager;
    Intent intent = null;
    String strAboutUs = "B2infosoft is Software Company in Jaipur.  The company was set up in 2013. B2infosoft is a company dedicated to supplying IT services to businesses.\n" +
            "\n" +
            "It is a global, IT firm full of ideas to develop and promote the online reputation of its clients. At B2infosoft, our vision is to scale along with our clients satisfactions.\n" + "\n" +
            "Office Address\n" +
            "Shaheed Amit Bhardwaj Marg,\n" +
            "\n" +
            "Malviya Nagar, Jaipur -302017,\n" +
            "\n" +
            "Rajasthan India\n";
    String strTermsCondition = "The terms B2infosoft individually and collectively refer to B2infosoft and the terms \"Visitor” ”User” refer to the users. \n" +
            "\n" +
            "This page states the Terms and Conditions under which you (Visitor) may visit this website (“Website”). Please read this page carefully. If you do not accept the Terms and Conditions stated here, we would request you to exit this site. The business, any of its business divisions and / or its subsidiaries, associate companies or subsidiaries to subsidiaries or such other investment companies (in India or abroad) reserve their respective rights to revise these Terms and Conditions at any time by updating this posting. You should visit this page periodically to re-appraise yourself of the Terms and Conditions, because they are binding on all users of this Website.\n" +
            "\n" +
            "USE OF CONTENT\n" +
            "\n" +
            "All logos, brands, marks headings, labels, names, signatures, numerals, shapes or any combinations thereof, appearing in this site, except as otherwise noted, are properties either owned, or used under licence, by the business and / or its associate entities who feature on this Website. The use of these properties or any other content on this site, except as provided in these terms and conditions or in the site content, is strictly prohibited.\n" +
            "\n" +
            "You may not sell or modify the content of this Website  or reproduce, display, publicly perform, distribute, or otherwise use the materials in any way for any public or commercial purpose without the respective organisation’s or entity’s written permission.\n" +
            "\n" +
            "ACCEPTABLE WEBSITE USE\n" +
            "\n" +
            "(A) Security Rules\n" +
            "Visitors are prohibited from violating or attempting to violate the security of the Web site, including, without limitation, (1) accessing data not intended for such user or logging into a server or account which the user is not authorised to access, (2) attempting to probe, scan or test the vulnerability of a system or network or to breach security or authentication measures without proper authorisation, (3) attempting to interfere with service to any user, host or network, including, without limitation, via means of submitting a virus or \"Trojan horse\" to the Website, overloading, \"flooding\", \"mail bombing\" or \"crashing\", or (4) sending unsolicited electronic mail, including promotions and/or advertising of products or services. Violations of system or network security may result in civil or criminal liability. The business and / or its associate entities will have the right to investigate occurrences that they suspect as involving such violations and will have the right to involve, and cooperate with, law enforcement authorities in prosecuting users who are involved in such violations.\n" +
            "\n" +
            "(B) General Rules\n" +
            "Visitors may not use the Web Site in order to transmit, distribute, store or destroy material (a) that could constitute or encourage conduct that would be considered a criminal offence or violate any applicable law or regulation, (b) in a manner that will infringe the copyright, trademark, trade secret or other intellectual property rights of others or violate the privacy or publicity of other personal rights of others, or (c) that is libellous, defamatory, pornographic, profane, obscene, threatening, abusive or hateful.\n" +
            "\n" +
            "INDEMNITY\n" +
            "\n" +
            "The User unilaterally agree to indemnify and hold harmless, without objection, the Company, its officers, directors, employees and agents from and against any claims, actions and/or demands and/or liabilities and/or losses and/or damages whatsoever arising from or resulting from their use of http://www.b2infosoft.com or their breach of the terms . \n" +
            "\n" +
            "LIABILITY\t\n" +
            "\n" +
            "User agrees that neither B2infosoft  nor its group companies, directors, officers or employee shall be liable for any direct or/and indirect or/and incidental or/and special or/and consequential or/and exemplary damages, resulting from the use or/and the inability to use the service or/and for cost of procurement of substitute goods or/and services or resulting from any goods or/and data or/and information or/and services purchased or/and obtained or/and messages received or/and transactions entered into through or/and from the service or/and resulting from unauthorized access to or/and alteration of user's transmissions or/and data or/and arising from any other matter relating to the service, including but not limited to, damages for loss of profits or/and use or/and data or other intangible, even if B2infosoft  has been advised of the possibility of such damages. \n" +
            "User further agrees that B2infosoft shall not be liable for any damages arising from interruption, suspension or termination of service, including but not limited to direct or/and indirect or/and incidental or/and special consequential or/and exemplary damages, whether such interruption or/and suspension or/and termination was justified or not, negligent or intentional, inadvertent or advertent. \n" +
            "User agrees that B2infosoft shall not be responsible or liable to user, or anyone, for the statements or conduct of any third party of the service. In sum, in no event shall Company's total liability to the User for all damages or/and losses or/and causes of action exceed the amount paid by the User to B2infosoft , if any, that is related to the cause of action.\n" +
            "\n" +
            "DISCLAIMER OF CONSEQUENTIAL DAMAGES\n" +
            "\n" +
            "In no event shall Company or any parties, organizations or entities associated with the corporate brand name us or otherwise, mentioned at this Website be liable for any damages whatsoever (including, without limitations, incidental and consequential damages, lost profits, or damage to computer hardware or loss of data information or business interruption) resulting from the use or inability to use the Website and the Website material, whether based on warranty, contract, tort, or any other legal theory, and whether or not, such organization or entities were advised of the possibility of such damages.\n";
    String strPrivacypolicy = "The terms B2infosoft individually and collectively refer to B2infosoft and the terms B2infosoft refer to the users. \n" +
            "This Privacy Policy is an electronic record in the form of an electronic contract formed under the information Technology Act, 2000 and the rules made thereunder and the amended provisions pertaining to electronic documents / records in various statutes as amended by the information Technology Act, 2000. This Privacy Policy does not require any physical, electronic or digital signature.\n" +
            "This Privacy Policy is a legally binding document between you and B2infosoft (both terms defined below). The terms of this Privacy Policy will be effective upon your acceptance of the same (directly or indirectly in electronic form, by clicking on the I accept tab or by use of the website or by other means) and will govern the relationship between you and B2infosoft for your use of the website http://www.b2infosoft.com .\n" +
            "This document is published and shall be construed in accordance with the provisions of the Information Technology (reasonable security practices and procedures and sensitive personal data of information) rules, 2011 under Information Technology Act, 2000; that require publishing of the Privacy Policy for collection, use, storage and transfer of sensitive personal data or information.\n" +
            "Please read this Privacy Policy carefully by using the Website, you indicate that you understand, agree and consent to this Privacy Policy. If you do not agree with the terms of this Privacy Policy, please do not use this Website. \n" +
            "By providing us your Information or by making use of the facilities provided by the Website, You hereby consent to the collection, storage, processing and transfer of any or all of Your Personal Information and Non-Personal Information by us as specified under this Privacy Policy. You further agree that such collection, use, storage and transfer of Your Information shall not cause any loss or wrongful gain to you or any other person.\n" +
            "USER INFORMATION \n" +
            "To avail certain services on our Websites, users are required to provide certain information for the registration process namely: - a) your name, b) email address, c) sex, d) age, e) PIN code, f) credit card or debit card details g) medical records and history h) sexual orientation, i) biometric information, j) password etc., and / or your occupation, interests, and the like. The Information as supplied by the users enables us to improve our sites and provide you the most user-friendly experience.\n" +
            "\n" +
            "All required information is service dependent and we may use the above said user information to, maintain, protect, and improve its services (including advertising services) and for developing new services\n" +
            "\n" +
            "Such information will not be considered as sensitive if it is freely available and accessible in the public domain or is furnished under the Right to Information Act, 2005 or any other law for the time being in force.\n" +
            "\n" +
            "COOKIES\n" +
            "To improve the responsiveness of the sites for our users, we may use \"cookies\", or similar electronic tools to collect information to assign each visitor a unique, random number as a User Identification (User ID) to understand the user's individual interests using the Identified Computer. Unless you voluntarily identify yourself (through registration, for example), we will have no way of knowing who you are, even if we assign a cookie to your computer. The only personal information a cookie can contain is information you supply (an example of this is when you ask for our Personalised Horoscope). A cookie cannot read data off your hard drive. Our advertisers may also assign their own cookies to your browser (if you click on their ads), a process that we do not control. \n" +
            "\n" +
            "Our web servers automatically collect limited information about your computer's connection to the Internet, including your IP address, when you visit our site. (Your IP address is a number that lets computers attached to the Internet know where to send you data -- such as the web pages you view.) Your IP address does not identify you personally. We use this information to deliver our web pages to you upon request, to tailor our site to the interests of our users, to measure traffic within our site and let advertisers know the geographic locations from where our visitors come. \n" +
            "LINKS TO THE OTHER SITES\n" +
            "Our policy discloses the privacy practices for our own web site only. Our site provides links to other websites also that are beyond our control. We shall in no way be responsible in way for your use of such sites.5. \n" +
            "INFORMATION SHARING\n" +
            "We shares the sensitive personal information to any third party without obtaining the prior consent of the user in the following limited circumstances:\n" +
            "\n" +
            "(a) When it is requested or required by law or by any court or governmental agency or authority to disclose, for the purpose of verification of identity, or for the prevention, detection, investigation including cyber incidents, or for prosecution and punishment of offences. These disclosures are made in good faith and belief that such disclosure is reasonably necessary for enforcing these Terms; for complying with the applicable laws and regulations. \n" +
            "\n" +
            "(b) We proposes to share such information within its group companies and officers and employees of such group companies for the purpose of processing personal information on its behalf. We also ensure that these recipients of such information agree to process such information based on our instructions and in compliance with this Privacy Policy and any other appropriate confidentiality and security measures.\n" +
            "\n" +
            "INFORMATION SECURITY\n" +
            "We take appropriate security measures to protect against unauthorized access to or unauthorized alteration, disclosure or destruction of data. These include internal reviews of our data collection, storage and processing practices and security measures, including appropriate encryption and physical security measures to guard against unauthorized access to systems where we store personal data.\n" +
            "\n" +
            "All information gathered on our Website is securely stored within our controlled database. The database is stored on servers secured behind a firewall; access to the servers is password-protected and is strictly limited. However, as effective as our security measures are, no security system is impenetrable. We cannot guarantee the security of our database, nor can we guarantee that information you supply will not be intercepted while being transmitted to us over the Internet. And, of course, any information you include in a posting to the discussion areas is available to anyone with Internet access. \n" +
            "\n" +
            "However the internet is an ever evolving medium. We may change our Privacy Policy from time to time to incorporate necessary future changes. Of course, our use of any information we gather will always be consistent with the policy under which the information was collected, regardless of what the new policy may be. \n" +
            "\n" +
            "Grievance Redressal\n" +
            "Redressal Mechanism: Any complaints, abuse or concerns with regards to content and or comment or breach of these terms shall be immediately informed to the designated Grievance Officer as mentioned below via in writing or through email signed with the electronic signature to B2infosoft (\"Grievance Officer\"). \n" +
            "\n" +
            "Mr. Gajendra (Grievance Officer)\n" +
            "http://www.b2infosoft.com .\n" +
            "Shaheed Amit Bhardwaj Marg,Malviya Nagar, Jaipur -302017,\n" +
            "Rajasthan India\n" +
            "Email:info@b2infosoft.in\n" +
            "Ph:  9772196777                   ";
    String strRefundAndCancel = "Our focus is complete customer satisfaction. In the event, if you are displeased with the services provided, we will refund back the money, provided the reasons are genuine and proved after investigation. Please read the fine prints of each deal before buying it, it provides all the details about the services or the product you purchase.\n" +
            "\n" +
            "In case of dissatisfaction from our services, clients have the liberty to cancel their projects and request a refund from us. Our Policy for the cancellation and refund will be as follows:\n" +
            " Cancellation Policy\n" +
            "\n" +
            "For Cancellations please contact the us at 9772196777\n" +
            "\n" +
            "Requests received later than  5 business days prior to the end of the current service period will be treated as cancellation of services for the next service period.\n" +
            "\n" +
            "Refund Policy\n" +
            "\n" +
            "We already provide 30 Days free trial to our customer. So no refund or cancellation available .";
    String strContactus = "\n\nEmail: " + "sharwan@b2infosoft.com\n\n" +
            "Contact: 9772196777" + ", " + "9772196666\n \n" +
            "Address : \n\n" +
            "E 126 SARASWATI NAGAR\n" +
            "JAGATPURA MALVIYA NAGAR\n" +
            "JAIPUR ";

    Fragment fragment = null;
    Bundle bundle = null;

    View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_terms_conditions, container, false);

        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        if (!hasPermissions(mContext, PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, PERMISSION_ALL);
        }

        initView();
        return view;
    }

    private void initView() {

        sessionManager = new SessionManager(mContext);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar_title = toolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText(mContext.getString(R.string.LEGALPOLICIES));

        toolbarManage();

        final Drawable upArrow = getResources().getDrawable(R.drawable.back_arrow);
        upArrow.setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);


        tvAboutUs = view.findViewById(R.id.tvAboutUs);
        tvPrivacy = view.findViewById(R.id.tvPrivacy);
        tvTerms = view.findViewById(R.id.tvTerms);
        tvRefundCancel = view.findViewById(R.id.tvRefundCancel);
        tvContactUs = view.findViewById(R.id.tvContactUs);

        tvAboutUs.setOnClickListener(this);
        tvPrivacy.setOnClickListener(this);
        tvTerms.setOnClickListener(this);
        tvRefundCancel.setOnClickListener(this);
        tvContactUs.setOnClickListener(this);

        tvDetails = view.findViewById(R.id.tvDetails);

        tvDetails.setText(strAboutUs);
        tvDetails.setMovementMethod(LinkMovementMethod.getInstance());
        tvDetails.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvAboutUs:
                tvAboutUs.setBackgroundResource(R.drawable.border_shape_rectangle_black);


                tvPrivacy.setBackgroundResource(R.color.color_light_white);
                tvTerms.setBackgroundResource(R.color.color_light_white);
                tvRefundCancel.setBackgroundResource(R.color.color_light_white);
                tvContactUs.setBackgroundResource(R.color.color_light_white);

                tvDetails.setText(strAboutUs);
                tvDetails.setMovementMethod(LinkMovementMethod.getInstance());
                tvDetails.setMovementMethod(new ScrollingMovementMethod());
                break;
            case R.id.tvPrivacy:

                tvPrivacy.setBackgroundResource(R.drawable.border_shape_rectangle_black);

                tvAboutUs.setBackgroundResource(R.color.color_light_white);
                tvTerms.setBackgroundResource(R.color.color_light_white);
                tvRefundCancel.setBackgroundResource(R.color.color_light_white);
                tvContactUs.setBackgroundResource(R.color.color_light_white);

                tvDetails.setText(strPrivacypolicy);
                tvDetails.setMovementMethod(LinkMovementMethod.getInstance());
                tvDetails.setMovementMethod(new ScrollingMovementMethod());
                break;
            case R.id.tvTerms:

                tvTerms.setBackgroundResource(R.drawable.border_shape_rectangle_black);

                tvAboutUs.setBackgroundResource(R.color.color_light_white);
                tvPrivacy.setBackgroundResource(R.color.color_light_white);
                tvRefundCancel.setBackgroundResource(R.color.color_light_white);
                tvContactUs.setBackgroundResource(R.color.color_light_white);

                tvDetails.setText(strTermsCondition);
                tvDetails.setMovementMethod(LinkMovementMethod.getInstance());
                tvDetails.setMovementMethod(new ScrollingMovementMethod());
                break;
            case R.id.tvRefundCancel:

                tvRefundCancel.setBackgroundResource(R.drawable.border_shape_rectangle_black);

                tvTerms.setBackgroundResource(R.color.color_light_white);
                tvAboutUs.setBackgroundResource(R.color.color_light_white);
                tvPrivacy.setBackgroundResource(R.color.color_light_white);
                tvContactUs.setBackgroundResource(R.color.color_light_white);

                tvDetails.setText(strRefundAndCancel);
                tvDetails.setMovementMethod(LinkMovementMethod.getInstance());
                tvDetails.setMovementMethod(new ScrollingMovementMethod());
                break;
            case R.id.tvContactUs:
                tvContactUs.setBackgroundResource(R.drawable.border_shape_rectangle_black);
                tvTerms.setBackgroundResource(R.color.color_light_white);
                tvAboutUs.setBackgroundResource(R.color.color_light_white);
                tvPrivacy.setBackgroundResource(R.color.color_light_white);
                tvRefundCancel.setBackgroundResource(R.color.color_light_white);

                tvDetails.setText(strContactus);
                tvDetails.setMovementMethod(LinkMovementMethod.getInstance());
                tvDetails.setMovementMethod(new ScrollingMovementMethod());
                break;
        }

    }

    public void toolbarManage() {
        toolbar.setNavigationIcon(R.drawable.ic_nav_drawer);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

    }

    @Override
    public void OnFragmentBackPressListener() {
        fragment = new DairyDeshboardFragment();
        goNextFragmentWithBackStack(mContext, fragment);
    }
}
