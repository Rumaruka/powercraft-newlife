package com.rumaruka.powercraft.api.gres.autoadd;

import com.rumaruka.powercraft.api.gres.PCGresComponent;

public interface IAutoComplete {

     void onStringAdded(PCGresComponent component, PCGresDocument document, PCGresDocumentLine line, String toAdd, int x, PCAutoCompleteDisplay info);

     void makeComplete(PCGresComponent component, PCGresDocument document, PCGresDocumentLine line, int x, PCAutoCompleteDisplay info);

     PCGresDocInfoCollector getInfoCollector();
}
