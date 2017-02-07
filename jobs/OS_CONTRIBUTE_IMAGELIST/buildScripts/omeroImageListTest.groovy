import java.util.Collection;
import java.util.Set;


/*
def printClassPath(classLoader) {
  println "$classLoader"
  classLoader.getURLs().each {url->
     println "- ${url.toString()}"
  }
  if (classLoader.parent) {
     printClassPath(classLoader.parent)
  }
}
printClassPath this.class.classLoader
*/

import omero.gateway.Gateway;
import omero.gateway.LoginCredentials;
import omero.gateway.SecurityContext;
import omero.gateway.facility.BrowseFacility;
import omero.gateway.model.ExperimenterData;
import omero.gateway.model.ProjectData;
import omero.gateway.model.PlateData;
import omero.gateway.model.PlateAcquisitionData;
import omero.gateway.model.WellData;
import omero.log.SimpleLogger;

username = 'jenkins'
password = 'jenkins123'
//username = 'root'
//password = 'omero'
host = '192.168.61.11'
port = 4064
LoginCredentials cred = new LoginCredentials(username, password, host, port);

//Create a simple Logger object which just writes
//to System.out or System.err
SimpleLogger simpleLogger = new SimpleLogger();

Gateway gateway = new Gateway(simpleLogger);
ExperimenterData user = gateway.connect(cred);
println 'connected!'

//for every subsequent call to the server you'll need the
//SecurityContext for a certain group; in this case create
//a SecurityContext for the user's default group.
SecurityContext ctx = new SecurityContext(user.getGroupId());


// ID of the plate we want to list the images of.
long plateId = 1
// The same plate ID in a list.
Collection<Long> plateIds = (Collection<Long>) new ArrayList<Long>()
plateIds.add(new Long(plateId))


println 1
BrowseFacility browse = gateway.getFacility(BrowseFacility.class);
PlateData plate;
plate = (PlateData) browse.findObject(ctx,PlateData.class,plateId,true)
println plate.getName()
Set<PlateAcquisitionData> pads = plate.getPlateAcquisitions()

println 2
Collection<PlateData> plates = browse.getPlates(ctx,plateIds)
Iterator<PlateData> i = plates.iterator();
while (i.hasNext()) {
    plate = i.next();
    //Do something
  	println plate.getName()
}

println 3
Collection<WellData> wells = browse.getWells(ctx, plateId);
Iterator<WellData> j = wells.iterator();
WellData well;
while (j.hasNext()) {
    well = j.next();
    //Do something
  	println 'well'
}

println 4
import omero.rtypes
import omero.api.IContainerPrx;
import omero.sys.ParametersI;
import omero.model.Dataset;
import omero.model.IObject;
import omero.model.Project;
import ome.model.screen.Plate;
import omero.gateway.model.DatasetData;
import omero.gateway.model.ProjectData;

IContainerPrx proxy = gateway.getPojosService(ctx);
ParametersI param = new ParametersI();
long userId = gateway.getLoggedInUser().getId();
param.exp(rtypes.rlong(userId));
//param.allExps()
//param.allGrps()
//Load the orphaned datasets.
param.orphan();
//Do not load the images.
param.noLeaves(); //indicate to load the images
//param.noLeaves(); //no images loaded, this is the default value.

// Is omero.model.screen.Plate the right class to search here?
List<IObject> results = proxy.loadContainerHierarchy(
	Plate.class.getName(), plateIds, param);
println results

// Iterator<IObject> i2 = results.iterator();
// ProjectData project;
// DatasetData dataset;
// IObject o;
// long datasetId = -1;
// while (i2.hasNext()) {
//       o = i2.next();
//       if (o instanceof Project) {
//           project = new ProjectData((Project) o);
//           println "Project:"+project.getId()+" "+ project.getName()
//       } else if (o instanceof Dataset) {
//           dataset = new DatasetData((Dataset) o);
//           println "Dataset:"+dataset.getId()+" "+ dataset.getName()
//         if (datasetId < 0) datasetId = dataset.getId();
//         //Image not loaded.
//       } else {
//         println o
//       }
// }

gateway.disconnect();
println "disconnected!"
