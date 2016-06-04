(ns com.ncpenterprises.clomosy.modules.protocols)

(defprotocol StatefulModule
  (update [this inputs])
  )

(defprotocol Module
  (inputs [])
  ()
  )
