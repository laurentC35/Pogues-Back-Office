package fr.insee.pogues.search.service;

import fr.insee.pogues.search.model.PoguesHit;
import fr.insee.pogues.search.model.PoguesItem;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;

import java.util.List;


public interface SearchService {

    IndexResponse save(String type, PoguesItem item) throws Exception;
    List<PoguesHit> searchByLabel(String label, String... types) throws Exception;
    DeleteResponse delete(String type, String id) throws Exception;
    List<PoguesHit> getSeries() throws Exception;
    List<PoguesHit> getOperations(String seriesId) throws Exception;
}
