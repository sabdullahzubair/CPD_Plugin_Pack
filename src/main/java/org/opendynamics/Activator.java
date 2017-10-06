package org.opendynamics;

import java.util.ArrayList;
import java.util.Collection;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

    protected Collection<ServiceRegistration> registrationList;

    public void start(BundleContext context) {
        registrationList = new ArrayList<ServiceRegistration>();

        //Register plugin here
        registrationList.add(context.registerService(AccountCreateCPDProvider.class.getName(), new AccountCreateCPDProvider(), null));
//        registrationList.add(context.registerService(AccountVerifyCPDProvider.class.getName(), new AccountVerifyCPDProvider(), null));
        registrationList.add(context.registerService(RegistrationCompleteCPDProvider.class.getName(), new RegistrationCompleteCPDProvider(), null));
        registrationList.add(context.registerService(AdminCPDPApprovalDataListAction.class.getName(), new AdminCPDPApprovalDataListAction(), null));
        registrationList.add(context.registerService(AccountCreateCPDUser.class.getName(), new AccountCreateCPDUser(), null));
        registrationList.add(context.registerService(AccountVerifyCPDUser.class.getName(), new AccountVerifyCPDUser(), null));
        registrationList.add(context.registerService(EventVerification.class.getName(), new EventVerification(), null));
        registrationList.add(context.registerService(ForgotPasswordKeyUpdate.class.getName(), new ForgotPasswordKeyUpdate(), null));
        registrationList.add(context.registerService(AccountCreateCPDCRMUser.class.getName(), new AccountCreateCPDCRMUser(), null));
        registrationList.add(context.registerService(AccountVerifyCPDCRMUser.class.getName(), new AccountVerifyCPDCRMUser(), null));
        registrationList.add(context.registerService(DummyWebService.class.getName(), new DummyWebService(), null));
        registrationList.add(context.registerService(MarkAttendanceCPDProvider.class.getName(), new MarkAttendanceCPDProvider(), null));
        registrationList.add(context.registerService(ExportDataList.class.getName(), new ExportDataList(), null));
        registrationList.add(context.registerService(AttendanceUploadExcel.class.getName(), new AttendanceUploadExcel(), null));
        registrationList.add(context.registerService(DeleteDataWithJogetUserDataListAction.class.getName(), new DeleteDataWithJogetUserDataListAction(), null));
        registrationList.add(context.registerService(AccountVerification.class.getName(), new AccountVerification(), null));
        registrationList.add(context.registerService(ImageCompressor.class.getName(), new ImageCompressor(), null));
    }

    public void stop(BundleContext context) {
        for (ServiceRegistration registration : registrationList) {
            registration.unregister();
        }
    }
}