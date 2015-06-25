package de.cismet.cids.custom.wupp.geocpm.api.transformer.impl;

import de.cismet.cids.custom.wupp.geocpm.api.GeoCPMProject;
import de.cismet.cids.custom.wupp.geocpm.api.transform.GeoCPMProjectTransformer;

/**
 *
 * @author martin.scholl@cismet.de
 * @version 1.0
 */
public class CountingLoopGeoCPMProjectTransformer implements GeoCPMProjectTransformer {
    
    private final long countTo;
    private final int expectedStep;
    

    public CountingLoopGeoCPMProjectTransformer(final long countTo, final int expectedStep) {
        this.countTo = countTo;
        this.expectedStep = expectedStep;
    }
    
    @Override
    public boolean accept(GeoCPMProject obj) {
        return obj.getAnnuality() == expectedStep;
    }

    @Override
    public GeoCPMProject transform(GeoCPMProject obj) {
        
        
        for(long i = 0; i < countTo; ++i) {
            // making it a little bit slower
            String.valueOf(this.toString() + this.hashCode() + obj.toString());
            if(Thread.interrupted()) {
                return obj;
            }
        }
        
        obj.setAnnuality(obj.getAnnuality() + 1);
        obj.setName((obj.getName() == null ? "" : obj.getName() + " ") + expectedStep);
        
        return obj;
    }
}