package camundaPilot.Schadenaufnahme;

import org.apache.ibatis.logging.LogFactory;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.consulting.process_test_coverage.ProcessTestCoverage;

/**
 * Test case starting an in-memory database-backed Process Engine.
 */
public class InMemoryH2Test {

	@Rule
	public ProcessEngineRule rule = new ProcessEngineRule(
			new StandaloneInMemProcessEngineConfiguration().buildProcessEngine());

	private static final String PROCESS_DEFINITION_KEY = "Schadenaufnahme";

	static {
		LogFactory.useSlf4jLogging(); // MyBatis
	}

	@Before
	public void setup() {
		init(rule.getProcessEngine());
	}

	@Test
	@Deployment(resources = "Schadenaufnahme.bpmn")
	public void testHappyPath() {
		// Prozessvariablen zuweisen
		Map<String, Object> variables = new HashMap<String, Object>();
		

		ProcessEngine processEngine = rule.getProcessEngine();
		
		// Zugriff und Start einer Ausführungsinstanz. Der Key ist die Id aus dem
		// Prozess-Editor
		ProcessInstance pi = processEngine().getRuntimeService()
				.startProcessInstanceByKey(PROCESS_DEFINITION_KEY);
		
		//Zugriff auf Taskliste / Bearbeitergruppe eines Tasks erfragen
		List<Task> tasks = processEngine.getTaskService().createTaskQuery().taskCandidateGroup("Agenten").list();
		
		
		
		assertThat(pi).task("Vertragsdaten_erfragen").hasCandidateGroup("Agenten");		
		assertThat(pi).isStarted()
				.isWaitingAt("Vertragsdaten_erfragen");
		
		//variablen direkt zuweisen und Task fetigstellen
		variables.put("Name", "VN-Name");
		variables.put("versicherungsnummer", "012400000");
		variables.put("autokennzeichen", "A B 123");
		Task task = processEngine.getTaskService().createTaskQuery().active().singleResult();
		processEngine.getTaskService().complete(task.getId(), variables);
		
		//welche Aktivität ist gerade aktiv?
		List<String> activities = processEngine.getRuntimeService().getActiveActivityIds(pi.getId());
		System.out.println(activities.get(0));
		
		//Jawohl, Vertrag gefunden
		processEngine.getRuntimeService().setVariable(pi.getId(), "contractFound", Boolean.TRUE);
		//und die Nachricht vom Webservice ist angekommen
		processEngine.getRuntimeService().createMessageCorrelation("Message_Vertrag_in").correlate();
		
		// Welcher Task ist jetzt aktiv?
		task = processEngine.getTaskService().createTaskQuery().active().singleResult();
		System.out.println(task.getName());
	}
	//
	// @After
	// public void calculateCoverageForAllTests() throws Exception {
	// ProcessTestCoverage.calculate(rule.getProcessEngine());
	// }

}
