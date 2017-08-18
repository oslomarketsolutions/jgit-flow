package com.atlassian.jgitflow.core.exception;

/*-
 * #%L
 * JGit-Flow core library
 * %%
 * Copyright (C) 2017 Atlassian Pty, LTD, Ultreia.io
 * %%
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
 * #L%
 */

/**
 * Superclass of all exceptions thrown by the API classes in
 * {@code com.atlassian.jgitflow.core}
 */
public class JGitFlowException extends Exception
{
    public JGitFlowException()
    {
        super();
    }

    public JGitFlowException(String message)
    {
        super(message);
    }

    public JGitFlowException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public JGitFlowException(Throwable cause)
    {
        super(cause);
    }

}