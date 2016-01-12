package org.virtual.refpub;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.sdmxsource.sdmx.api.model.beans.codelist.CodelistBean;
import org.virtual.refpub.utilities.Table2SdmxTransform;
import org.virtualrepository.impl.Type;
import org.virtualrepository.sdmx.SdmxCodelist;
import org.virtualrepository.spi.Importer;

@Singleton
public class SdmxImporter implements Importer<SdmxCodelist,CodelistBean> {

	private final BaseImporter importer;

	@Inject
	public SdmxImporter(BaseImporter importer)  {
		this.importer = importer;
	}
	
	@Override
	public Type<SdmxCodelist> type() {
		return SdmxCodelist.type;
	}

	@Override
	public Class<CodelistBean> api() {
		return CodelistBean.class;
	}

	@Override
	public CodelistBean retrieve(SdmxCodelist asset) throws Exception {
		return new Table2SdmxTransform().toSdmx(asset, importer.retrieve(asset));
	}
}
