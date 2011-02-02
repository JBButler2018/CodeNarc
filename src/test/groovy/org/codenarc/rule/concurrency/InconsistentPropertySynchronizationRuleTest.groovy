/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codenarc.rule.concurrency

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule

/**
 * Tests for InconsistentPropertySynchronizationRule
 *
 * @author 'Hamlet D'Arcy'
 * @version $Revision: 329 $ - $Date: 2010-04-29 04:20:25 +0200 (Thu, 29 Apr 2010) $
 */
class InconsistentPropertySynchronizationRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == "InconsistentPropertySynchronization"
    }

    void testSuccessScenario() {
        final SOURCE = '''
            class Person {
                String name
                Date birthday
                boolean deceased
                boolean parent

                synchronized setName(String name) {
                    this.name = name
                }
                synchronized String getName() {
                    name
                }

                void setBirthday(Date birthday) {
                    this.birthday = birthday
                }

                String getBirthday() {
                    birthday
                }

                synchronized void setDeceased(boolean deceased) {
                    this.deceased = deceased
                }

                synchronized boolean isDeceased() {
                    deceased
                }

                synchronized void setParent(boolean parent) {
                    this.parent = parent
                }

                synchronized boolean isParent() {
                    parent
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testUnsynchronizedStringGetMethod() {
        final SOURCE = '''
            class Person {
                String name

                synchronized setName(String name) {
                    this.name = name
                }
                // violation, get method should be synchronized
                String getName() {
                    name
                }
            }
        '''
        assertSingleViolation(SOURCE, 9, 'String getName()',
                'The setter method setName is synchronized but the getter method getName is not')
    }

    void testUnsynchronizedDateSetMethod() {
        final SOURCE = '''
            class Person {
                Date birthday

                // violation, set method should be synchronized
                void setBirthday(Date birthday) {
                    this.birthday = birthday
                }

                synchronized String getBirthday() {
                    birthday
                }
            }
        '''
        assertSingleViolation(SOURCE, 6, 'void setBirthday',
                'The getter method getBirthday is synchronized but the setter method setBirthday is not')
    }

    void testUnsynchronizedBooleanSetMethod() {
        final SOURCE = '''
            class Person {
                boolean deceased

                // violation, set method should be synchronized
                void setDeceased(boolean deceased) {
                    this.deceased = deceased
                }

                synchronized boolean isDeceased() {
                    deceased
                }
            }
        '''
        assertSingleViolation(SOURCE, 6, 'void setDeceased(',
                'The getter method isDeceased is synchronized but the setter method setDeceased is not')
    }

    void testUnsynchronizedBooleanGetMethod() {
        final SOURCE = '''
            class Person {
                boolean parent

                synchronized void setParent(boolean parent) {
                    this.parent = parent
                }

                // violation, get method should be synchronized
                boolean isParent() {
                    parent
                }
            }
        '''
        assertSingleViolation(SOURCE, 10, 'boolean isParent()',
                'The setter method setParent is synchronized but the getter method isParent is not')
    }

    protected Rule createRule() {
        new InconsistentPropertySynchronizationRule()
    }
}