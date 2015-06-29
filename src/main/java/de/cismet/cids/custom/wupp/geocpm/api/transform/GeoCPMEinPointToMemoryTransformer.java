/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wupp.geocpm.api.transform;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.Arrays;

import de.cismet.cids.custom.wupp.geocpm.api.GeoCPMConstants;
import de.cismet.cids.custom.wupp.geocpm.api.GeoCPMProject;
import de.cismet.cids.custom.wupp.geocpm.api.entity.Point;

/**
 * Point parser based on GeoCPM API Spec v1.2 (05.10.2011).
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
@Slf4j
public class GeoCPMEinPointToMemoryTransformer implements GeoCPMProjectTransformer {

    //~ Static fields/initializers ---------------------------------------------

    private static final String POINT_LINE_REGEX = "(?:\\d+)(?:" + GeoCPMConstants.DEFAULT_FIELD_SEP
                + "-?\\d+\\.\\d\\d\\d){3}";

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   obj  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public boolean accept(final GeoCPMProject obj) {
        return (obj != null) && (obj.getGeocpmEin() != null) && obj.getGeocpmEin().canRead();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   obj  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public GeoCPMProject transform(final GeoCPMProject obj) {
        //J-
        // jalopy only supports java 1.6
        try(final BufferedReader br = new BufferedReader(new FileReader(obj.getGeocpmEin()))) {
            final Point[] points = br.lines()
                    .filter(line -> line.matches(POINT_LINE_REGEX))
                    .map(line -> {
                        final String[] s = line.split(GeoCPMConstants.DEFAULT_FIELD_SEP);
                        return new Point(
                                Integer.parseInt(s[0]),
                                Double.parseDouble(s[1]),
                                Double.parseDouble(s[2]),
                                Double.parseDouble(s[3]));
                    })
                    .toArray(Point[]::new);

            obj.setPoints(Arrays.asList(points));

            return obj;
        } catch (final IOException ex) {
            throw new TransformException("cannot read geocpm ein file", ex); // NOI18N
        }
        //J+
    }
}