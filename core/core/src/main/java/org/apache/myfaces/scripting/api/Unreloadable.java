package org.apache.myfaces.scripting.api;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          <p>
 *          This interface can be used by managed beans
 *          to prevent reloading.
 *          </p>
 *          <p>
 *          The managed bean reloading strategy is very aggressive
 *          it drops all dynamic managed beans out of the corresponding
 *          scopes if possible to enforce a clean reload wherever possible.
 *          </p>
 */
public interface Unreloadable {
}
