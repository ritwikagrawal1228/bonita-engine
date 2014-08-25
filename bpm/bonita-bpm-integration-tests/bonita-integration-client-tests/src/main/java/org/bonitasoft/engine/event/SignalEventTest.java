package org.bonitasoft.engine.event;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.bonitasoft.engine.CommonAPITest;
import org.bonitasoft.engine.api.ProcessRuntimeAPI;
import org.bonitasoft.engine.bpm.bar.BusinessArchive;
import org.bonitasoft.engine.bpm.bar.BusinessArchiveBuilder;
import org.bonitasoft.engine.bpm.flownode.ActivityInstanceCriterion;
import org.bonitasoft.engine.bpm.flownode.EventInstance;
import org.bonitasoft.engine.bpm.flownode.HumanTaskInstance;
import org.bonitasoft.engine.bpm.flownode.IntermediateCatchEventInstance;
import org.bonitasoft.engine.bpm.flownode.IntermediateThrowEventInstance;
import org.bonitasoft.engine.bpm.process.DesignProcessDefinition;
import org.bonitasoft.engine.bpm.process.ProcessDefinition;
import org.bonitasoft.engine.bpm.process.ProcessInstance;
import org.bonitasoft.engine.bpm.process.impl.ProcessDefinitionBuilder;
import org.bonitasoft.engine.identity.User;
import org.bonitasoft.engine.test.TestStates;
import org.bonitasoft.engine.test.annotation.Cover;
import org.bonitasoft.engine.test.annotation.Cover.BPMNConcept;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SignalEventTest extends CommonAPITest {

    private User john = null;

    @Before
    public void setUp() throws Exception {
        loginOnDefaultTenantWithDefaultTechnicalLogger();
        john = createUser(USERNAME, PASSWORD);
    }

    @After
    public void tearDown() throws Exception {
        logoutOnTenant();
        loginOnDefaultTenantWithDefaultTechnicalLogger();
        if (john != null) {
            getIdentityAPI().deleteUser(john.getId());
        }
        logoutOnTenant();
    }

    @Cover(classes = { EventInstance.class }, concept = BPMNConcept.EVENTS, keywords = { "Event", "Signal Event", "Start event", "End event", "Send", "Receive" }, story = "Send a signal from an end event of a process to a start event of an other process.", jira = "")
    @Test
    public void sendSignal() throws Exception {
        final BusinessArchiveBuilder archiveBuilder = new BusinessArchiveBuilder();

        ProcessDefinitionBuilder builder = new ProcessDefinitionBuilder();
        builder.createNewInstance("SayGO", "1.0").addActor(ACTOR_NAME).addStartEvent("Start").addUserTask("step1", ACTOR_NAME).addEndEvent("End")
                .addSignalEventTrigger("GO").addTransition("Start", "step1").addTransition("step1", "End");
        archiveBuilder.createNewBusinessArchive().setProcessDefinition(builder.done());
        final BusinessArchive endSignalArchive = archiveBuilder.done();

        builder = new ProcessDefinitionBuilder();
        builder.createNewInstance("GetGO", "1.0").addActor(ACTOR_NAME).addStartEvent("StartOnSignal").addSignalEventTrigger("GO")
                .addUserTask("Task1", ACTOR_NAME).addTransition("StartOnSignal", "Task1");
        archiveBuilder.createNewBusinessArchive().setProcessDefinition(builder.done());
        final BusinessArchive startSignalArchive = archiveBuilder.done();

        final ProcessDefinition processDefinitionWithStartSignal = deployAndEnableProcessWithActor(startSignalArchive, ACTOR_NAME, john);
        final ProcessDefinition processDefinitionWithEndSignal = deployAndEnableProcessWithActor(endSignalArchive, ACTOR_NAME, john);

        logoutOnTenant();
        loginOnDefaultTenantWith(USERNAME, PASSWORD);

        // Check that the process with trigger signal on start is not started, before send signal
        final ProcessInstance processInstanceWithEndSignal = getProcessAPI().startProcess(processDefinitionWithEndSignal.getId());
        waitForUserTask("step1", processInstanceWithEndSignal);
        checkNbOfProcessInstances(1);

        List<HumanTaskInstance> taskInstances = getProcessAPI().getPendingHumanTaskInstances(john.getId(), 0, 10, ActivityInstanceCriterion.NAME_ASC);
        assertEquals(1, taskInstances.size());
        assertEquals("step1", taskInstances.get(0).getName());

        // Send signal
        assignAndExecuteStep(taskInstances.get(0), john.getId());
        waitForProcessToFinish(processInstanceWithEndSignal.getId());

        // Check that the process with trigger signal on start is started, after send signal
        waitForUserTask("Task1");
        taskInstances = getProcessAPI().getPendingHumanTaskInstances(john.getId(), 0, 10, ActivityInstanceCriterion.NAME_ASC);
        assertEquals(1, taskInstances.size());
        assertEquals("Task1", taskInstances.get(0).getName());

        disableAndDeleteProcess(processDefinitionWithStartSignal, processDefinitionWithEndSignal);
    }

    @Cover(classes = { EventInstance.class, IntermediateCatchEventInstance.class }, concept = BPMNConcept.EVENTS, keywords = { "Event", "Signal event",
            "Intermediate catch event", "End event", "Send", "Receive" }, story = "Send a signal from an end event of a process to an intermediate catch event of an other process.", jira = "")
    @Test
    public void sendIntermediateCatchSignal() throws Exception {
        final BusinessArchiveBuilder archiveBuilder = new BusinessArchiveBuilder();

        ProcessDefinitionBuilder builder = new ProcessDefinitionBuilder();
        builder.createNewInstance("SayGO", "1.0").addStartEvent("Start").addEndEvent("End").addSignalEventTrigger("GO").addTransition("Start", "End");
        archiveBuilder.createNewBusinessArchive().setProcessDefinition(builder.done());
        final BusinessArchive endSignalArchive = archiveBuilder.done();

        builder = new ProcessDefinitionBuilder();
        builder.createNewInstance("GetGO", "1.0").addActor(ACTOR_NAME).addStartEvent("Start").addIntermediateCatchEvent("OnSignal").addSignalEventTrigger("GO")
                .addUserTask("Task1", ACTOR_NAME).addTransition("Start", "OnSignal").addTransition("OnSignal", "Task1");
        archiveBuilder.createNewBusinessArchive().setProcessDefinition(builder.done());
        final BusinessArchive startSignalArchive = archiveBuilder.done();

        final ProcessDefinition startSignal = deployAndEnableProcessWithActor(startSignalArchive, ACTOR_NAME, john);
        final ProcessDefinition endSignal = deployAndEnableProcess(endSignalArchive);

        logoutOnTenant();
        loginOnDefaultTenantWith(USERNAME, PASSWORD);
        final ProcessInstance instance = getProcessAPI().startProcess(startSignal.getId());
        waitForEvent(instance, "OnSignal", TestStates.WAITING);

        getProcessAPI().startProcess(endSignal.getId());
        waitForUserTask("Task1");

        disableAndDeleteProcess(startSignal, endSignal);
    }

    @Cover(classes = { EventInstance.class, IntermediateThrowEventInstance.class }, concept = BPMNConcept.EVENTS, keywords = { "Event", "Signal event",
            "Intermediate catch event", "Start event", "Send", "Receive" }, story = "Send a sigal from an intermediate throw event of a process to a start event of an other process.", jira = "")
    @Test
    public void sendIntermadiateThrowSignal() throws Exception {
        final BusinessArchiveBuilder archiveBuilder = new BusinessArchiveBuilder();

        ProcessDefinitionBuilder builder = new ProcessDefinitionBuilder();
        final String intermediate = "Intermediate";
        builder.createNewInstance("SayGO", "1.0").addStartEvent("Start").addIntermediateThrowEvent(intermediate).addSignalEventTrigger("GO").addEndEvent("End")
                .addTransition("Start", intermediate).addTransition(intermediate, "End");
        archiveBuilder.createNewBusinessArchive().setProcessDefinition(builder.done());
        final BusinessArchive endSignalArchive = archiveBuilder.done();

        builder = new ProcessDefinitionBuilder();
        builder.createNewInstance("GetGO", "1.0").addActor(ACTOR_NAME).addStartEvent("StartOnSignal").addSignalEventTrigger("GO")
                .addUserTask("Task1", ACTOR_NAME).addTransition("StartOnSignal", "Task1");
        archiveBuilder.createNewBusinessArchive().setProcessDefinition(builder.done());
        final BusinessArchive startSignalArchive = archiveBuilder.done();

        final ProcessDefinition startSignal = deployAndEnableProcessWithActor(startSignalArchive, ACTOR_NAME, john);
        final ProcessDefinition endSignal = deployAndEnableProcess(endSignalArchive);

        logoutOnTenant();
        loginOnDefaultTenantWith(USERNAME, PASSWORD);

        getProcessAPI().startProcess(endSignal.getId());
        waitForUserTask("Task1");

        disableAndDeleteProcess(startSignal, endSignal);
    }

    @Cover(classes = { ProcessRuntimeAPI.class }, concept = BPMNConcept.EVENTS, keywords = { "signal", "throw event", "send signal", "start event" }, jira = "ENGINE-455")
    @Test
    public void sendSignalViaAPIToStartSignalEvent() throws Exception {
        final ProcessDefinitionBuilder builder = new ProcessDefinitionBuilder();
        builder.createNewInstance("GetGO", "1.0").addActor(ACTOR_NAME).addStartEvent("StartOnSignal").addSignalEventTrigger("GO")
                .addUserTask("Task1", ACTOR_NAME).addTransition("StartOnSignal", "Task1");
        final DesignProcessDefinition startSignalDef = builder.done();

        final ProcessDefinition startSignal = deployAndEnableProcessWithActor(startSignalDef, ACTOR_NAME, john);
        logoutOnTenant();
        loginOnDefaultTenantWith(USERNAME, PASSWORD);

        getProcessAPI().sendSignal("GO");
        waitForUserTask("Task1");

        disableAndDeleteProcess(startSignal);
    }

    @Cover(classes = { ProcessRuntimeAPI.class }, concept = BPMNConcept.EVENTS, keywords = { "signal", "throw event", "send signal", "intermediate catch event" }, jira = "ENGINE-455")
    @Test
    public void sendSignalViaAPIToIntermediateSignalEvent() throws Exception {

        final ProcessDefinitionBuilder builder = new ProcessDefinitionBuilder();
        builder.createNewInstance("GetGO", "1.0").addActor(ACTOR_NAME).addStartEvent("Start").addIntermediateCatchEvent("OnSignal").addSignalEventTrigger("GO")
                .addUserTask("Task1", ACTOR_NAME).addTransition("Start", "OnSignal").addTransition("OnSignal", "Task1");
        final ProcessDefinition intermediateSignal = deployAndEnableProcessWithActor(builder.done(), ACTOR_NAME, john);
        logoutOnTenant();
        loginOnDefaultTenantWith(USERNAME, PASSWORD);

        final ProcessInstance instance = getProcessAPI().startProcess(intermediateSignal.getId());
        waitForEvent(instance, "OnSignal", TestStates.WAITING);

        getProcessAPI().sendSignal("GO");
        waitForUserTask("Task1");

        disableAndDeleteProcess(intermediateSignal);
    }

}
