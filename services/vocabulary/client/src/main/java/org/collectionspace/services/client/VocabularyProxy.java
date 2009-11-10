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

import org.collectionspace.services.vocabulary.VocabulariesCommonList;
import org.collectionspace.services.vocabulary.VocabularyitemsCommonList;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartOutput;

/**
 * @version $Revision:$
 */
@Path("/vocabularies/")
@Produces({"multipart/mixed"})
@Consumes({"multipart/mixed"})
public interface VocabularyProxy {

    // List Vocabularies
    @GET
    @Produces({"application/xml"})
    ClientResponse<VocabulariesCommonList> readList();

    //(C)reate
    @POST
    ClientResponse<Response> create(MultipartOutput multipart);

    //(R)ead
    @GET
    @Path("/{csid}")
    ClientResponse<MultipartInput> read(@PathParam("csid") String csid);

    //(U)pdate
    @PUT
    @Path("/{csid}")
    ClientResponse<MultipartInput> update(@PathParam("csid") String csid, MultipartOutput multipart);

    //(D)elete
    @DELETE
    @Path("/{csid}")
    ClientResponse<Response> delete(@PathParam("csid") String csid);

    // List Items
    @GET
    @Produces({"application/xml"})
    @Path("/{vcsid}/items/")
    ClientResponse<VocabularyitemsCommonList> readItemList(@PathParam("vcsid") String vcsid);

    //(C)reate Item
    @POST
    @Path("/{vcsid}/items/")
    ClientResponse<Response> createItem(@PathParam("vcsid") String vcsid, MultipartOutput multipart);

    //(R)ead
    @GET
    @Path("/{vcsid}/items/{csid}")
    ClientResponse<MultipartInput> readItem(@PathParam("vcsid") String vcsid, @PathParam("csid") String csid);

    //(U)pdate
    @PUT
    @Path("/{vcsid}/items/{csid}")
    ClientResponse<MultipartInput> updateItem(@PathParam("vcsid") String vcsid, @PathParam("csid") String csid, MultipartOutput multipart);

    //(D)elete
    @DELETE
    @Path("/{vcsid}/items/{csid}")
    ClientResponse<Response> deleteItem(@PathParam("vcsid") String vcsid, @PathParam("csid") String csid);
}