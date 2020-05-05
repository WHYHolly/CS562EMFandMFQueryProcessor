with mo_tot as(
  select prod, month, sum(quant) as mo_s
  from sales
  where year = 2004
  group by prod, month
),
yr_tot as(
  select prod, sum(quant) as yr_s
  from sales
  where year = 2004
  group by prod
)

select mo_tot.prod, month, (mo_s + 0.0) / yr_s
from mo_tot, yr_tot
where mo_tot.prod = yr_tot.prod
order by mo_tot.prod