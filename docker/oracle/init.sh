if ( echo "SELECT USERNAME FROM all_users where USERNAME='EDP_REMOCRA';" | \
  sqlplus sqlplus / as sysdba@//localhost:1521/EDPSIGDEV | grep EDP_REMOCRA > /dev/null ); then
  echo "Scripts déjà appliqués"
else
  echo "Scripts : application" && \
  echo " Création User " && \
  cat /home/oracle/initdb/sql/init1.sql | sqlplus / as sysdba@//localhost:1521/EDPSIGDEV &&  \
  echo " Insertion données " && \
  cat /home/oracle/initdb/sql/init2.sql | sqlplus EDP_REMOCRA/1234@//localhost:1521/EDPSIGDEV && \
  echo "Scripts : appliqués"
fi
