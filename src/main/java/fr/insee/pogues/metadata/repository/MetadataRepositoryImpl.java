package fr.insee.pogues.metadata.repository;

import fr.insee.pogues.metadata.client.MetadataClient;
import fr.insee.pogues.metadata.model.ColecticaItem;
import fr.insee.pogues.metadata.model.ColecticaItemRefList;
import fr.insee.pogues.metadata.model.Unit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MetadataRepositoryImpl implements MetadataRepository {

	@Autowired
	MetadataClient metadataClient;

	@Override
	public ColecticaItem findById(String id) throws Exception {
		return metadataClient.getItem(id);
	}

	@Override
	public ColecticaItemRefList getChildrenRef(String id) throws Exception {
		return metadataClient.getChildrenRef(id);
	}

	@Override
	public List<ColecticaItem> getItems(ColecticaItemRefList refs) throws Exception {
		return metadataClient.getItems(refs);
	}

	@Override
	public List<Unit> getUnits() throws Exception {
		return metadataClient.getUnits();
	}

	@Override
	public String getDDIDocument(String id) throws Exception {
		return metadataClient.getDDIDocument(id);
	}

	@Override
	public String getCodeList(String id) throws Exception {
		return metadataClient.getCodeList(id);
	}
}
