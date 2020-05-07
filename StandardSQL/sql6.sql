-- find each prod the median sale (assume odd number of sales)

with tot as(
	select prod, count(*) as tot_cnt
	from sales
	group by prod
),
cmp as(
	select y.prod, y.quant, count(x.quant) as cmp_cnt
	from sales as x, sales as y
	where x.prod = y.prod and x.quant < y.quant
	group by y.prod, y.quant
)

select cmp.prod, cmp.quant
from tot, cmp
where tot.prod = cmp.prod and cmp_cnt*2 = tot_cnt