package de.cismet.cids.custom.wupp.geocpm.api;

import de.cismet.cids.custom.wupp.geocpm.api.transform.NoopGeoCPMImportTransformer;
import de.cismet.cids.custom.wupp.geocpm.api.transform.NoopGeoCPMProjectTransformer;
import de.cismet.cids.custom.wupp.geocpm.api.transformer.impl.CountingLoopGeoCPMImportTransformer;
import de.cismet.cids.custom.wupp.geocpm.api.transformer.impl.Sleep500GeoCPMProjectTransformer;
import de.cismet.commons.utils.ProgressEvent;
import de.cismet.commons.utils.ProgressListener;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;
/**
 *
 * @author martin.scholl@cismet.de
 * @version 1.0
 */
public class GeoCPMImportOrchestratorNGTest {

    public GeoCPMImportOrchestratorNGTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.setProperty("org.openide.util.Lookup", MockLookup.class.getName()); 
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        System.clearProperty("org.openide.util.Lookup");
    }

    @BeforeMethod
    public void setUpMethod() throws Exception {
        MockLookup.setUp();
    }

    @AfterMethod
    public void tearDownMethod() throws Exception {
    }

    public void printCurrentTestName() {
        System.out.println("TEST " + new Throwable().getStackTrace()[1].getMethodName());
    }

    /**
     * Test of doImport method, of class GeoCPMImportOrchestrator.
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testDoImport_Object_nullObject() {
        printCurrentTestName();
        
        new GeoCPMImportOrchestrator().doImport(null);
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testDoImport_Object_ProgressListener_nullObject() {
        printCurrentTestName();
        
        new GeoCPMImportOrchestrator().doImport(null, null);
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testDoImport_Configuration_Object_ProgressListener_nullConfiguration() {
        printCurrentTestName();
        
        new GeoCPMImportOrchestrator().doImport(null, null, null);
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testDoImport_Configuration_Object_ProgressListener_nullObject() {
        printCurrentTestName();
        
        new GeoCPMImportOrchestrator().doImport(new Properties(), null, null);
    }

    /**
     * Test of doImport method, of class GeoCPMImportOrchestrator.
     */
    @Test
    public void testDoImport_defaultConfig_noLookup() throws Exception {
        printCurrentTestName();
        
        final GeoCPMImportOrchestrator o = new GeoCPMImportOrchestrator();
        
        Field field = o.getClass().getDeclaredField("defaultConfiguration");
        field.setAccessible(true);
        final Properties config = (Properties)field.get(o);
        
        assertEquals(config.getProperty(GeoCPMConstants.CFG_PIPELINE_PARALLEL_EXECS), "1");
        assertEquals(config.getProperty(GeoCPMConstants.CFG_IMPORTER_FQCN), 
                NoopGeoCPMImportTransformer.class.getCanonicalName());
        assertEquals(config.getProperty(GeoCPMConstants.CFG_PIPELINE_IMPORTER_FQCN_PREFIX + "1"), 
                NoopGeoCPMProjectTransformer.class.getCanonicalName());
    }

    @Test
    public void testDoImport_defaultConfig_importLookup() throws Exception {
        printCurrentTestName();
        
        MockLookup.add(new CountingLoopGeoCPMImportTransformer(5, 500));
        
        final GeoCPMImportOrchestrator o = new GeoCPMImportOrchestrator();
        
        Field field = o.getClass().getDeclaredField("defaultConfiguration");
        field.setAccessible(true);
        final Properties config = (Properties)field.get(o);
        
        assertEquals(config.getProperty(GeoCPMConstants.CFG_PIPELINE_PARALLEL_EXECS), "1");
        assertEquals(config.getProperty(GeoCPMConstants.CFG_IMPORTER_FQCN), 
                CountingLoopGeoCPMImportTransformer.class.getCanonicalName());
        assertEquals(config.getProperty(GeoCPMConstants.CFG_PIPELINE_IMPORTER_FQCN_PREFIX + "1"), 
                NoopGeoCPMProjectTransformer.class.getCanonicalName());
    }
    
    @Test
    public void testDoImport_defaultConfig_projectLookup() throws Exception {
        printCurrentTestName();
        
        MockLookup.add(new Sleep500GeoCPMProjectTransformer());
        
        final GeoCPMImportOrchestrator o = new GeoCPMImportOrchestrator();
        
        Field field = o.getClass().getDeclaredField("defaultConfiguration");
        field.setAccessible(true);
        final Properties config = (Properties)field.get(o);
        
        assertEquals(config.getProperty(GeoCPMConstants.CFG_PIPELINE_PARALLEL_EXECS), "1");
        assertEquals(config.getProperty(GeoCPMConstants.CFG_IMPORTER_FQCN), 
                NoopGeoCPMImportTransformer.class.getCanonicalName());
        assertEquals(config.getProperty(GeoCPMConstants.CFG_PIPELINE_IMPORTER_FQCN_PREFIX + "1"), 
                Sleep500GeoCPMProjectTransformer.class.getCanonicalName());
    }
    
    @Test
    public void testDoImport_defaultConfig_importAndProjectLookup() throws Exception {
        printCurrentTestName();
        
        MockLookup.add(new Sleep500GeoCPMProjectTransformer());
        MockLookup.add(new CountingLoopGeoCPMImportTransformer());
        
        final GeoCPMImportOrchestrator o = new GeoCPMImportOrchestrator();
        
        Field field = o.getClass().getDeclaredField("defaultConfiguration");
        field.setAccessible(true);
        final Properties config = (Properties)field.get(o);
        
        assertEquals(config.getProperty(GeoCPMConstants.CFG_PIPELINE_PARALLEL_EXECS), "1");
        assertEquals(config.getProperty(GeoCPMConstants.CFG_IMPORTER_FQCN), 
                CountingLoopGeoCPMImportTransformer.class.getCanonicalName());
        assertEquals(config.getProperty(GeoCPMConstants.CFG_PIPELINE_IMPORTER_FQCN_PREFIX + "1"), 
                Sleep500GeoCPMProjectTransformer.class.getCanonicalName());
    }
    
    @Test
    public void testDoImport_defaultConfig_runNoop() throws Exception {
        printCurrentTestName();
        
        final GeoCPMImportOrchestrator o = new GeoCPMImportOrchestrator();
        Future<ProgressEvent.State> f = o.doImport(new Object());
        
        assertEquals(f.get(), ProgressEvent.State.FINISHED);
    }
    
    @Test
    public void testDoImport_defaultConfig_runNoopProgressL() throws Exception {
        printCurrentTestName();
        
        final GeoCPMImportOrchestrator o = new GeoCPMImportOrchestrator();
        
        final ProgressL progL = new ProgressL();
        Future<ProgressEvent.State> f = o.doImport(new Object(), progL);
        
        f.get();
        
        awaitState(progL, ProgressEvent.State.FINISHED, 5000);
    }
    
    @Test(expectedExceptions = CancellationException.class)
    public void testDoImport_defaultConfig_cancelInterruptImport() throws Exception {
        printCurrentTestName();
        
        MockLookup.add(new CountingLoopGeoCPMImportTransformer(5, 10000));
        
        final GeoCPMImportOrchestrator o = new GeoCPMImportOrchestrator();
        
        Future<ProgressEvent.State> f = o.doImport(new Object());
        f.cancel(true);
        f.get();
    }
    
    @Test(expectedExceptions = CancellationException.class)
    public void testDoImport_defaultConfig_cancelNoInterruptImport() throws Exception {
        printCurrentTestName();
        
        MockLookup.add(new CountingLoopGeoCPMImportTransformer(5, 10000));
        
        final GeoCPMImportOrchestrator o = new GeoCPMImportOrchestrator();
        
        Future<ProgressEvent.State> f = o.doImport(new Object());
        f.cancel(false);
        f.get();
    }
    
    @Test()
    public void testDoImport_defaultConfig_cancelInterruptImportProgressL() throws Exception {
        printCurrentTestName();
        
        MockLookup.add(new CountingLoopGeoCPMImportTransformer());
        
        final GeoCPMImportOrchestrator o = new GeoCPMImportOrchestrator();
        
        final ProgressL progL = new ProgressL();
        Future<ProgressEvent.State> f = o.doImport(new Object(), progL);
        // give the importer thread a chance to actually start working, otherwise it would not be scheduled at all
        Thread.sleep(100);
        f.cancel(true);
        
        awaitState(progL, ProgressEvent.State.CANCELED, 5000);
    }
    
    // plain and simple active waiting
    private void awaitState(ProgressL progL, ProgressEvent.State state, long timeout) throws Exception {
        long start = System.currentTimeMillis();
                
        while((System.currentTimeMillis() - start) < timeout) {
            if(progL.lastEvent != null && progL.lastEvent.getState().equals(state)) {
                // we got it
                return;
            }
            
            Thread.sleep(100);
        }
        
        fail("did not receive event with state " + state + ", waited for " + timeout + " ms");
    }
    
    private static final class ProgressL implements ProgressListener {
        
        ProgressEvent lastEvent = null;

        @Override
        public void progress(ProgressEvent pe) {
            lastEvent = pe;
        }
    }
    
    // copy of http://forums.netbeans.org/viewtopic.php?t=52754.
    public static final class MockLookup extends AbstractLookup {
        public static MockLookup instance;
        protected final InstanceContent content;

        public MockLookup() {
            this(new InstanceContent());
            instance = this;
        }

        private MockLookup(InstanceContent content) {
            super(content);
            this.content = content;
        }

        /**
         * Call this method from your TestCase setUp method to dispose of any
         * content added by a previous test e.g. mock objects.
         */
        public static void setUp() {
            // Retrieve default Lookup to force MockLookup instance to be constructed
            Lookup lookup = Lookup.getDefault();
            // Ensure that MockLookup or one of its subclasses has been registered
            if (lookup != instance) {
                String message = MockLookup.class.getSimpleName() + " not registered.\n" +
                        "Please ensure system property \"org.openide.util.Lookup\" has been set.";
                System.out.println(message);
                fail(message);
            }
            // Reset the contents so that the next test can run in isolation
            if (instance != null) {
                instance.content.set(Collections.emptyList(), null);
                instance.initialize(); // allows subclasses to define content to always include
            }
        }

        /**
         * Add Object {@code obj} to lookup.
         */
        public static void add(Object obj) {
            instance.content.add(obj);
        } 
    }
}