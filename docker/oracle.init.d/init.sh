if ( echo "SELECT USERNAME FROM all_users where USERNAME='EDP_REMOCRA';" | \
  sqlplus system/oracle@//127.0.0.1:1521/edpsigdev | grep EDP_REMOCRA > /dev/null ); then
  echo "Scripts déjà appliqués"
else
  echo "Scripts : application" && \
  cat /oracle.init.d/sql/init1.sql | sqlplus system/oracle@127.0.0.1/edpsigdev &&  \
  cat /oracle.init.d/sql/init2.sql | sqlplus EDP_REMOCRA/1234@127.0.0.1/edpsigdev && \
  echo "Scripts : appliqués"
fi
