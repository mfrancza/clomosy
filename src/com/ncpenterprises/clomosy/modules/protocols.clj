(ns com.ncpenterprises.clomosy.modules.protocols)


(defprotocol module
  (inputs []
    )

  (initial-state [])

  )

(defprotocol update
  (inputs [])
  (update-state [system-state module-state &inputs])
  )

(defprotocol output
  (inputs [])
  (update-state [system-state module-state &inputs])
  )




