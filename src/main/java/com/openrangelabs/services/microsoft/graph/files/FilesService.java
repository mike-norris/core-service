package com.openrangelabs.services.microsoft.graph.files;

import java.io.*;
import java.net.URL;

import com.amazonaws.util.IOUtils;
import com.microsoft.graph.concurrency.ChunkedUploadProvider;
import com.microsoft.graph.concurrency.IProgressCallback;
import com.microsoft.graph.models.extensions.*;
import com.microsoft.graph.models.extensions.File;
import com.microsoft.graph.requests.extensions.*;
import com.openrangelabs.services.microsoft.graph.files.model.GetDriveItemsResponse;
import com.openrangelabs.services.microsoft.graph.files.model.UploadDriveItemResponse;
import com.openrangelabs.services.organization.bloxops.dao.BloxopsOrganizationDAO;
import com.openrangelabs.services.microsoft.graph.files.entity.OneDriveItem;
import com.openrangelabs.services.microsoft.graph.files.model.*;
import com.openrangelabs.services.microsoft.graph.GraphAuthProvider;
import com.openrangelabs.services.report.service.ReportService;
import com.openrangelabs.services.roster.bloxops.dao.RosterBloxopsDAO;
import com.openrangelabs.services.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@Transactional
public class FilesService {
    
    @Value("${microsoft.driveId}")
    String driveId;

    GraphAuthProvider graphAuthProvider;
    BloxopsOrganizationDAO bloxopsOrganizationDAO;
    ReportService reportService;
    UserService userService;
    RosterBloxopsDAO rosterBloxopsDAO;

    String ERROR = "Graph client has not been initialized.";

    @Autowired
    FilesService(GraphAuthProvider graphAuthProvider ,BloxopsOrganizationDAO bloxopsOrganizationDAO,ReportService reportService,UserService userService,RosterBloxopsDAO rosterBloxopsDAO){
        this.graphAuthProvider = graphAuthProvider;
        this.bloxopsOrganizationDAO = bloxopsOrganizationDAO;
        this.reportService = reportService;
        this.userService = userService;
        this.rosterBloxopsDAO = rosterBloxopsDAO;
    }

    public OneDriveItem setDriveItem(DriveItem driveItem, String orgCode){
        OneDriveItem oneDriveItem = new OneDriveItem();
        Calendar calendar = driveItem.lastModifiedDateTime;
        Calendar lastModifiedCalendar = driveItem.lastModifiedDateTime;
        File file = driveItem.file;
        Date createdDate = calendar.getTime();

        oneDriveItem.setCreatedDT(createdDate.toString());
        oneDriveItem.setId(driveItem.id);
        oneDriveItem.setName(driveItem.name);
        oneDriveItem.setSize(driveItem.size);
        if(orgCode != null) {
            oneDriveItem.setOrgCode(orgCode);
        }

        oneDriveItem.setLastModified(String.valueOf(lastModifiedCalendar.getTime()));

        if(driveItem.folder != null){
            oneDriveItem.setIsFolder(true);
        }else{
            oneDriveItem.setIsFolder(false);
            if(file != null){
                oneDriveItem.setFileType(file.mimeType);
            }
        }
        return oneDriveItem;
    }

    public GetDriveItemsResponse getDriveFolder(String itemId, String orgCode) {
        try{
            IGraphServiceClient graphClient = graphAuthProvider.getAuthProvider();
            if (graphClient == null) {
                log.error(ERROR);
                return new GetDriveItemsResponse(null, ERROR);
            }

            IDriveItemCollectionPage folder = graphClient.drives().byId(driveId).items().byId(itemId).children().buildRequest().get();
            List<DriveItem> driveItemList = folder.getCurrentPage();
            List<OneDriveItem> oneDriveItemsList = new ArrayList<>();

            for(DriveItem driveItem : driveItemList){
                String name = driveItem.name;
                if(!name.contains("PRIVATE")) {
                    OneDriveItem oneDriveItem = setDriveItem(driveItem, orgCode);
                    oneDriveItemsList.add(oneDriveItem);
                }
            }

            return new GetDriveItemsResponse(oneDriveItemsList, null);
        }catch(Exception e){
            log.error("Error getting drive item contents ID:" +driveId);
            log.error(e.getMessage());
            return new GetDriveItemsResponse(null, "Error getting drive item contents.");
        }
    }

    public GetDriveItemsResponse searchItem(String searchText) {
        try{
            IGraphServiceClient graphClient = graphAuthProvider.getAuthProvider();
            if (graphClient == null) {
                log.error(ERROR);
                return new GetDriveItemsResponse(null, ERROR);
            }
            if(searchText.contains("PRIVATE")){
                return new GetDriveItemsResponse(null,"You do not have permission to view these files.");
            }

            IDriveItemSearchCollectionPage search= graphClient.me().drives().byId(driveId).root().search(searchText).buildRequest().get();
            List<DriveItem> driveItemList = search.getCurrentPage();
            List<OneDriveItem> oneDriveItemsList = new ArrayList<>();

            for(DriveItem driveItem : driveItemList){
                    OneDriveItem oneDriveItem = setDriveItem(driveItem,null);
                    oneDriveItemsList.add(oneDriveItem);

            }
            log.info("Searching drive for items.");
            return new GetDriveItemsResponse(oneDriveItemsList, null);
        }catch(Exception e){
            log.error("Error searching drive for items.");
            log.error(e.getMessage());
            return new GetDriveItemsResponse(null, "Error searching drive for items");
        }

    }
    IProgressCallback<DriveItem> callback = new IProgressCallback<DriveItem>() {
        @Override
        // Called after each slice of the file is uploaded
        public void progress(final long current, final long max) {
            log.info(
                    String.format("Uploaded %d bytes of %d total bytes", current, max)
            );
        }

        @Override
        public void success(final DriveItem result) {
            log.info(
                    String.format("Uploaded file with ID: %s", result.id)
            );
        }

        @Override
        public void failure(com.microsoft.graph.core.ClientException ex) {
            log.info(
                    String.format("Error uploading file: %s", ex.getMessage())
            );
        }

    };

    public UploadDriveItemResponse uploadItem(String documentFolder, String downloadLink, String documentName) {

        try{
            IGraphServiceClient graphClient = graphAuthProvider.getAuthProvider();
            if (graphClient == null) {
                log.error(ERROR);
                return new UploadDriveItemResponse(false, ERROR);
            }
            String path = "CUST/"+ documentFolder+"/"+  documentName +".pdf";
            log.info("UPLOADING FILE TO " + path);
            UploadSession uploadSession1 = graphClient
                    .drives()
                    .byId(driveId)
                    .root()
                    .itemWithPath(path)
                    .createUploadSession(new DriveItemUploadableProperties())
                    .buildRequest()
                    .post();

            InputStream stream = getInputStream(downloadLink);

            ChunkedUploadProvider<DriveItem> chunkedUploadProvider =
                    new ChunkedUploadProvider<DriveItem>(uploadSession1, graphClient, stream, stream.available(), DriveItem.class);

            // Config parameter is an array of integers
            // customConfig[0] indicates the max slice size
            // Max slice size must be a multiple of 320 KiB
            int[] customConfig = { 320 * 1024 };

            // Do the upload
            chunkedUploadProvider.upload(callback, customConfig);

            log.info("Document Uploaded Successfully.");
            return new UploadDriveItemResponse(true, null);
        }catch(Exception e){
            log.error(e.getMessage());
            log.error("Error uploading drive item.");
            return new UploadDriveItemResponse(false, "Error searching drive for items");
        }

    }

    private InputStream getInputStream(String url) throws IOException {
        log.info("Writing file from sign now to local temp file.");
        byte[] b = IOUtils.toByteArray((new URL(url)).openStream());

        String tempLocation = System.getProperty("java.io.tmpdir");
        java.io.File file = new java.io.File(tempLocation+"temp.pdf");
        FileUtils.writeByteArrayToFile(file,b);
        log.info("Writing temp file to InputStream.");
        InputStream inputStream = new FileInputStream(file);
        if(file.delete()){
            log.info("Deleted temp file.");
        }else{
            log.error("File could not be deleted.");
        }
        log.info("Returning input stream.");
        return inputStream;
    }

    public GetDriveItemsResponse getCompanyFolder(String folderName) {
        try {
            GetDriveItemsResponse getDriveItemResponse = searchItem(folderName);
            List<OneDriveItem> driveItemList = getDriveItemResponse.getDriveItemList();
            OneDriveItem driveItem = driveItemList.get(0);
            GetDriveItemsResponse getDriveFolder = getDriveFolder(driveItem.getId(), folderName);

            return getDriveFolder;
        }catch (Exception e){
            log.error(e.getMessage());
            return new GetDriveItemsResponse(null ,"Error retrieving one drive folder for org "+ folderName);
        }
    }

    public UploadDriveItemResponse uploadDriveItem(String folderName, String documentName, String downloadLink) {

        try {
            GetDriveItemsResponse getDriveItemResponse = searchItem(folderName);
            List<OneDriveItem> driveItemList = getDriveItemResponse.getDriveItemList();
            OneDriveItem driveItem = driveItemList.get(0);
            uploadItem(folderName , downloadLink, documentName);

            return new UploadDriveItemResponse( true,null);
        }catch (Exception e){
            log.error(e.getMessage());
            return new UploadDriveItemResponse(false ,"Error uploading document : "+ documentName);
        }
    }
}
