package com.generalidevelopment.hit2013;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.roo.addon.test.RooIntegrationTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@Configurable
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext*.xml")
@Transactional
@RooIntegrationTest(entity = Item.class)
public class ItemIntegrationTest {

    @Test
    public void testMarkerMethod() {
    }

	@Autowired
    private ItemDataOnDemand dod;

	@Test
    public void testCountItems() {
        Assert.assertNotNull("Data on demand for 'Item' failed to initialize correctly", dod.getRandomItem());
        long count = Item.countItems();
        Assert.assertTrue("Counter for 'Item' incorrectly reported there were no entries", count > 0);
    }

	@Test
    public void testFindItem() {
        Item obj = dod.getRandomItem();
        Assert.assertNotNull("Data on demand for 'Item' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'Item' failed to provide an identifier", id);
        obj = Item.findItem(id);
        Assert.assertNotNull("Find method for 'Item' illegally returned null for id '" + id + "'", obj);
        Assert.assertEquals("Find method for 'Item' returned the incorrect identifier", id, obj.getId());
    }

	@Test
    public void testFindAllItems() {
        Assert.assertNotNull("Data on demand for 'Item' failed to initialize correctly", dod.getRandomItem());
        long count = Item.countItems();
        Assert.assertTrue("Too expensive to perform a find all test for 'Item', as there are " + count + " entries; set the findAllMaximum to exceed this value or set findAll=false on the integration test annotation to disable the test", count < 250);
        List<Item> result = Item.findAllItems();
        Assert.assertNotNull("Find all method for 'Item' illegally returned null", result);
        Assert.assertTrue("Find all method for 'Item' failed to return any data", result.size() > 0);
    }

	@Test
    public void testFindItemEntries() {
        Assert.assertNotNull("Data on demand for 'Item' failed to initialize correctly", dod.getRandomItem());
        long count = Item.countItems();
        if (count > 20) count = 20;
        int firstResult = 0;
        int maxResults = (int) count;
        List<Item> result = Item.findItemEntries(firstResult, maxResults);
        Assert.assertNotNull("Find entries method for 'Item' illegally returned null", result);
        Assert.assertEquals("Find entries method for 'Item' returned an incorrect number of entries", count, result.size());
    }

	@Test
    public void testFlush() {
        Item obj = dod.getRandomItem();
        Assert.assertNotNull("Data on demand for 'Item' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'Item' failed to provide an identifier", id);
        obj = Item.findItem(id);
        Assert.assertNotNull("Find method for 'Item' illegally returned null for id '" + id + "'", obj);
        boolean modified =  dod.modifyItem(obj);
        Integer currentVersion = obj.getVersion();
        obj.flush();
        Assert.assertTrue("Version for 'Item' failed to increment on flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }

	@Test
    public void testMergeUpdate() {
        Item obj = dod.getRandomItem();
        Assert.assertNotNull("Data on demand for 'Item' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'Item' failed to provide an identifier", id);
        obj = Item.findItem(id);
        boolean modified =  dod.modifyItem(obj);
        Integer currentVersion = obj.getVersion();
        Item merged = obj.merge();
        obj.flush();
        Assert.assertEquals("Identifier of merged object not the same as identifier of original object", merged.getId(), id);
        Assert.assertTrue("Version for 'Item' failed to increment on merge and flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }

	@Test
    public void testPersist() {
        Assert.assertNotNull("Data on demand for 'Item' failed to initialize correctly", dod.getRandomItem());
        Item obj = dod.getNewTransientItem(Integer.MAX_VALUE);
        Assert.assertNotNull("Data on demand for 'Item' failed to provide a new transient entity", obj);
        Assert.assertNull("Expected 'Item' identifier to be null", obj.getId());
        obj.persist();
        obj.flush();
        Assert.assertNotNull("Expected 'Item' identifier to no longer be null", obj.getId());
    }

	@Test
    public void testRemove() {
        Item obj = dod.getRandomItem();
        Assert.assertNotNull("Data on demand for 'Item' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'Item' failed to provide an identifier", id);
        obj = Item.findItem(id);
        obj.remove();
        obj.flush();
        Assert.assertNull("Failed to remove 'Item' with identifier '" + id + "'", Item.findItem(id));
    }
}
