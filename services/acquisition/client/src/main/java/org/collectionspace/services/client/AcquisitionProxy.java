package org.collectionspace.services.client;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.collectionspace.services.acquisition.Acquisition;
import org.collectionspace.services.acquisition.AcquisitionList;
import org.jboss.resteasy.client.ClientResponse;

/**
 * @version $Revision:$
 */
@Path("/acquisitions/")
@Produces({"application/xml"})
@Consumes({"application/xml"})
public interface AcquisitionProxy {

    @GET
    ClientResponse<AcquisitionList> readList();

    //(C)reate
    @POST
    ClientResponse<Response> create(Acquisition co);

    //(R)ead
    @GET
    @Path("/{csid}")
    ClientResponse<Acquisition> read(@PathParam("csid") String csid);

    //(U)pdate
    @PUT
    @Path("/{csid}")
    ClientResponse<Acquisition> update(@PathParam("csid") String csid, Acquisition co);

    //(D)elete
    @DELETE
    @Path("/{csid}")
    ClientResponse<Response> delete(@PathParam("csid") String csid);
}