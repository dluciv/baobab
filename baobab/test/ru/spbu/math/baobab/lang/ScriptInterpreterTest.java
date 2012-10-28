package ru.spbu.math.baobab.lang;

import junit.framework.TestCase;

/**
 * Tests for script interpreter
 * 
 * @author vloginova
 */

public class ScriptInterpreterTest extends TestCase {
	public void testCommandCorrectness() {
		ScriptInterpreter interpteter = new ScriptInterpreter();
		try {
			interpteter.process("define timeslot 1st_try 12:12 to 12:40 on even Monday");
			interpteter.process("���������� �������� 1st_try �� 12:12 �� 12:13 � �������� �����������");
		} catch (Exception e) {
			fail("command's not correct");
		}
	}
}
