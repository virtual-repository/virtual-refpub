package org.virtual.refpub.utilities;

import static java.lang.String.format;

import org.sdmxsource.sdmx.api.model.beans.codelist.CodelistBean;
import org.sdmxsource.sdmx.api.model.mutable.codelist.CodeMutableBean;
import org.sdmxsource.sdmx.api.model.mutable.codelist.CodelistMutableBean;
import org.sdmxsource.sdmx.sdmxbeans.model.mutable.codelist.CodeMutableBeanImpl;
import org.sdmxsource.sdmx.sdmxbeans.model.mutable.codelist.CodelistMutableBeanImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtualrepository.Asset;
import org.virtualrepository.tabular.Column;
import org.virtualrepository.tabular.Row;
import org.virtualrepository.tabular.Table;

public class Table2SdmxTransform {
	private static Logger log = LoggerFactory.getLogger(Table2SdmxTransform.class);

	public CodelistBean toSdmx(Asset asset, Table table) {
		log.info("transforming codelist " + asset.name() + " to sdmx");

		CodelistMutableBean codelist = new CodelistMutableBeanImpl();

		codelist.setAgencyId("FAO");

		codelist.setId(asset.name());
		codelist.addName("en", asset.name());

		Column codeColumn = table.columns().get(0);

		// knowledge of current tables for a minimal transform
		for(Row row : table) {
			CodeMutableBean code = new CodeMutableBeanImpl();
			code.setId(adaptCode(row.get(codeColumn)));

			String name = name(row, "E");
			if(name == null) continue;

			code.addName("en", name);

			name = name(row, "F");
			if(name != null) code.addName("fr", name);

			name = name(row, "S");
			if(name != null) code.addName("es", name);
			
			name = name(row, "A");
			if(name != null) code.addName("ar", name);

			name = name(row, "C");
			if(name != null) code.addName("zh", name);

			name = name(row, "R");
			if(name != null) code.addName("ru", name);

			codelist.addItem(code);
		}

		return codelist.getImmutableInstance();
	}

	private static String name_format = "%s_%s";

	private String name(Row row, String suffix) {
		String name = row.get(format(name_format, "name", suffix.toLowerCase()));

		if(name == null) {
			name = row.get(format(name_format, "short_name", suffix.toLowerCase()));
			
			if(name == null) name = row.get(format(name_format, "long_name", suffix.toLowerCase()));
		}

		return name;

	}

	private String adaptCode(String code) {
		// TODO add to this simple adaptation
		return code.replace(".", "_");
	}
}
